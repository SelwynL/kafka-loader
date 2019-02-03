package org.selwyn.kafkaloader.avro

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.scalatest.{Matchers, WordSpecLike}
import io.circe.Json
import io.circe.parser._
import scala.io.Source

class AvroCodecSpec extends WordSpecLike with Matchers {

  def schemaFromResource(filename: String): Schema = {
    new Parser()
      .parse(Source.fromURL(getClass.getResource(s"/$filename")).mkString)
  }

  val userSchema: Schema       = schemaFromResource("user-schema.avsc")
  val userAvroCodec: AvroCodec = new AvroCodec(userSchema)

  val lensConfigSchema: Schema = schemaFromResource("lens-config-schema.avsc")
  val lensAvroCodec: AvroCodec = new AvroCodec(lensConfigSchema)

  "AvroCodec" must {

    "decode Json to GenericRecord" in {
      val json = parse("""
        {
            "id": 1234,
            "name": "john doe",
            "level": "OWNER",
            "email" : {
              "string" : "john.doe@gmail.com"
            }
        }
        """).getOrElse(throw new RuntimeException("Unable parse user json string"))

      val expected: GenericRecord = new GenericData.Record(userSchema)
      expected.put("id", 1234)
      expected.put("name", "john doe")
      expected.put("level", "OWNER")
      expected.put("email", "john.doe@gmail.com")

      val result = userAvroCodec.decodeJson(json)
      result.isRight should be(true)
      result.right.get should be(expected)
    }

    "fallback to default value for decode Json to GenericRecord" in {
      // missing email field should default to predefined value
      val json = parse("""
        {
            "id": 1234,
            "name": "john doe",
            "level": "OWNER"
        }
        """).getOrElse(throw new RuntimeException("Unable parse json string"))

      val expected: GenericRecord = new GenericData.Record(userSchema)
      expected.put("id", 1234)
      expected.put("name", "john doe")
      expected.put("level", "OWNER")
      expected.put("email", null)

      val result = userAvroCodec.decodeJson(json)
      result.isRight should be(true)
      result.right.get should be(expected)
    }

    "encode GenericRecord to Json" in {
      val userRecord: GenericRecord = new GenericData.Record(userSchema)
      val levelSchema: Schema       = userSchema.getField("level").schema

      userRecord.put("id", 1234)
      userRecord.put("name", "john doe")
      userRecord.put("level", new GenericData.EnumSymbol(levelSchema, "OWNER"))
      userRecord.put("email", "john.doe@gmail.com")

      val expected = parse("""
        {
            "id": 1234,
            "name": "john doe",
            "level": "OWNER",
            "email" : {
              "string" : "john.doe@gmail.com"
            }
        }
        """)

      val encoded: Either[Throwable, Json] =
        userAvroCodec.encodeJson(userRecord, pretty = true)
      encoded should be(expected)
    }

    "fallback to default value for encode GenericRecord to Json" in {
      // missing email field should default to predefined value
      val userRecord: GenericRecord = new GenericData.Record(userSchema)
      val levelSchema: Schema       = userSchema.getField("level").schema

      userRecord.put("id", 1234)
      userRecord.put("name", "john doe")
      userRecord.put("level", new GenericData.EnumSymbol(levelSchema, "OWNER"))

      val expected = parse("""
        {
            "id": 1234,
            "name": "john doe",
            "level": "OWNER",
            "email": null
        }
        """)

      val encoded: Either[Throwable, Json] =
        userAvroCodec.encodeJson(userRecord, pretty = true)
      encoded should be(expected)
    }

    "encode complex GenericRecord to Json" in {
      import collection.JavaConverters._

      val lensSchema: Schema = lensConfigSchema.getField("lens").schema

      val fieldSchema: Schema =
        lensSchema.getField("fields").schema.getElementType
      val fieldTypeSchema: Schema = fieldSchema.getField("fieldType").schema
      val indexTypeSchema: Schema = fieldSchema.getField("indexType").schema
      val sortOrderSchema: Schema = fieldSchema.getField("sortOrder").schema

      val field: GenericRecord = new GenericData.Record(fieldSchema)
      field.put("name", "employeeId")
      field.put("indexType", new GenericData.EnumSymbol(indexTypeSchema, "LOOKUP_INDEX"))
      field.put("fieldType", new GenericData.EnumSymbol(fieldTypeSchema, "STRING_FIELD"))
      field.put("description", "Employee ID")
      field.put("required", true)
      field.put("sortOrder", new GenericData.EnumSymbol(sortOrderSchema, "IGNORE_SORT_ORDER"))

      val lens: GenericRecord = new GenericData.Record(lensSchema)
      lens.put("name", "PHONEBOOK_Employee")
      lens.put("version", "0.1")
      lens.put("fields", List(field).asJava)

      val streamReader: Map[String, String] = Map(
        "auto.offset.reset"   -> "latest",
        "topic.subscriptions" -> "test-employee",
        "group.id"            -> "test-employee-group",
        "bootstrap.servers"   -> "localhost:9093",
        "schema.registry.url" -> "http://localhost:8081"
      )

      val operationalStoreSchema: Schema =
        lensConfigSchema.getField("operationalStore").schema
      val storeTypeSchema: Schema =
        operationalStoreSchema.getField("storeType").schema
      val regionSchema: Schema = operationalStoreSchema.getField("region").schema
      val operationalStore: GenericRecord =
        new GenericData.Record(operationalStoreSchema)
      operationalStore.put("storeType", new GenericData.EnumSymbol(storeTypeSchema, "REST"))
      operationalStore.put("region", new GenericData.EnumSymbol(regionSchema, "US_EAST_1"))
      operationalStore.put("connectionString", "http://localhost:6379")

      val lensConfigRecord: GenericRecord =
        new GenericData.Record(lensConfigSchema)
      lensConfigRecord.put("lensKey", "someKey")
      lensConfigRecord.put("lens", lens)
      lensConfigRecord.put("streamReader", streamReader.asJava)
      lensConfigRecord.put("operationalStore", operationalStore)

      val expected = parse(
        """
          |{
          |  "lensKey" : "someKey",
          |  "lens" : {
          |    "name" : "PHONEBOOK_Employee",
          |    "version" : "0.1",
          |    "fields" : [
          |      {
          |        "name" : "employeeId",
          |        "indexType" : "LOOKUP_INDEX",
          |        "fieldType" : "STRING_FIELD",
          |        "description" : "Employee ID",
          |        "required" : true,
          |        "sortOrder" : "IGNORE_SORT_ORDER"
          |      }
          |    ],
          |    "includeRecord" : null
          |  },
          |  "streamReader" : {
          |    "auto.offset.reset" : "latest",
          |    "topic.subscriptions" : "test-employee",
          |    "group.id" : "test-employee-group",
          |    "bootstrap.servers" : "localhost:9093",
          |    "schema.registry.url" : "http://localhost:8081"
          |  },
          |  "operationalStore" : {
          |    "storeType" : "REST",
          |    "region" : "US_EAST_1",
          |    "connectionString" : "http://localhost:6379",
          |    "clientId" : null,
          |    "clientSecret" : null
          |  }
          |}
        """.stripMargin
      )

      val encoded: Either[Throwable, Json] =
        lensAvroCodec.encodeJson(lensConfigRecord, pretty = true)
      encoded should be(expected)
    }
  }
}
