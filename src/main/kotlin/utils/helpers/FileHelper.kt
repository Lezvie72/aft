package utils.helpers

import io.qameta.allure.Attachment
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang.RandomStringUtils
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random


object FileHelper {
    data class Result(
        val uploadAmount: String,
        val uploadAmount1: String,
        val uploadAmount2: String,
        val barNo: String,
        val barNo1: String,
        val barNo2: String
    )

    @Attachment
    fun createNomenclature(fileName: String): Result {
        val barNo = "Q${RandomStringUtils.randomNumeric(4)}"
        val barNo1 = "Q${RandomStringUtils.randomNumeric(4)}"
        val barNo2 = "Q${RandomStringUtils.randomNumeric(4)}"

        val uploadAmount =
            "0.0${Random.nextInt(8) + 1}${Random.nextInt(8) + 1}"// делается для того чтобы избежать загрузки веса равного 0.000
        val uploadAmount1 = "0.0${Random.nextInt(8) + 1}${Random.nextInt(8) + 1}"
        val uploadAmount2 = "0.0${Random.nextInt(8) + 1}${Random.nextInt(8) + 1}"


        val CSV_File_Path =
            "src/test/resources/${fileName}.csv"
        val writer = Files.newBufferedWriter(Paths.get(CSV_File_Path))
        try {
            val csvPrinter = CSVPrinter(
                writer, CSVFormat.DEFAULT
                    .withHeader(
                        "Bar No",
                        "Amount to tokenization",
                        "Fine weight",
                        "Fineness",
                        "Gross weight",
                        "Refiner",
                        "Storage place",
                        "Year of smelting",
                        "Country of origin of metal",
                        "Specific mine",
                        "Year of mining",
                        "Price valuation",
                        "quality issues",
                        "Field N",
                        "Field N+1"
                    )
            )
            csvPrinter.printRecord(
                barNo,
                uploadAmount,
                uploadAmount,
                "9999",
                uploadAmount,
                "ASAHI REF CANADA LTD",
                "Zurich",
                "2000",
                "ASAHI REF CANADA LTD",
                "Zurich",
                "1991",
                "2...",
                "3...",
                "4...5...",
                "6..."
            )
            csvPrinter.printRecord(
                barNo1,
                uploadAmount1,
                uploadAmount1,
                "9999",
                uploadAmount1,
                "ASAHI REF CANADA LTD",
                "Zurich",
                "2000",
                "ASAHI REF CANADA LTD",
                "Zurich",
                "1991",
                "2...",
                "3...",
                "4...5...",
                "6..."
            )
            csvPrinter.printRecord(
                barNo2,
                uploadAmount2,
                uploadAmount2,
                "9999",
                uploadAmount2,
                "ASAHI REF CANADA LTD",
                "Zurich",
                "2000",
                "ASAHI REF CANADA LTD",
                "Zurich",
                "1991",
                "2...",
                "3...",
                "4...5...",
                "6..."
            )
            attachCsvFile(fileName, CSV_File_Path)
            csvPrinter.flush()
            csvPrinter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //так как операция редемптион принимает сумму ту которую загрузили в файле то ее
        return Result(uploadAmount, uploadAmount1, uploadAmount2, barNo, barNo1, barNo2)
    }

    fun deleteFile(fileName: String) {
        val CSV_File_Path =
            "src\\test\\resources\\${fileName}"
        val file = File(CSV_File_Path)
        if (file.exists()) {
            file.deleteRecursively()
            print("File $fileName deleted successfully")
        } else {
            print("Failed to delete the file $fileName")
        }
    }


}