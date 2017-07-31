package org.hdfgroup.spark.hdf5

import org.apache.commons.io.FilenameUtils
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.{ min, max }
import org.apache.spark.sql.types._

class STACTests extends FunTestSuite {

  val stm3file = getClass.getResource("LAAA.h5").toString
  val stm3dir = FilenameUtils.getFullPathNoEndSeparator(
    getClass.getResource("LAAA.h5").getPath)

  val bidtest = "/LAAA/Quotes/Bid"
  val qtrtest = "/LAAA/Quarter/QuotesStartBlock"
  val montest = "/LAAA/Month/QuotesStartBlock"
  val wktest = "/LAAA/Week/QuotesStartBlock"
  val qtroffset = 5670000L
  val monoffset = 6930000L
  val wkoffset = 7440000L

  test("Get the year high bid") {
    val biddf = sqlContext.read.hdf5(stm3file, bidtest)

    assert(biddf.schema === makeSchema(FloatType))

    val bid = biddf.agg(max(biddf("value"))).head
    val expected = Row(19.999998f)
    checkRowsEqual(bid, expected)
  }

  test("Get the last quarter high bid") {
    val biddf = sqlContext.read.hdf5(stm3file, bidtest)
    val qtrdf = biddf.where(biddf("index") >= qtroffset)
    val bid = qtrdf.agg(max(qtrdf("value"))).head
    val expected = Row(19.99998f)
    checkRowsEqual(bid, expected)
  }

  test("Get the last month's high bid") {
    val biddf = sqlContext.read.hdf5(stm3file, bidtest)
    val mondf = biddf.where(biddf("index") >= monoffset)
    val bid = mondf.agg(max(mondf("value"))).head
    val expected = Row(19.999973f)
    checkRowsEqual(bid, expected)
  }

  test("Get the last week's high bid") {
    val biddf = sqlContext.read.hdf5(stm3file, bidtest)
    val wkdf = biddf.where(biddf("index") >= wkoffset)
    val bid = wkdf.agg(max(wkdf("value"))).head
    val expected = Row(19.999973f)
    checkRowsEqual(bid, expected)
  }

}
