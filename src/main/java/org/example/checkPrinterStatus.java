package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class checkPrinterStatus {
    public static String checkPrinterStatus() {

        //String printerName = "\\\\FSMDS\\P-HR";
        ProcessBuilder builder = new ProcessBuilder("powershell.exe", "$printerStatus = get-wmiobject -class win32_printer | Select-Object Name, PrinterStatus | where {$_.Name -eq '\\\\fsmds\\P-Mentenanta'}\n" +
                "$printerStatus.PrinterStatus");

        String fullStatus = "0";
        Process reg;
        builder.redirectErrorStream(true);
        try {
            reg = builder.start();
            fullStatus = getStringFromInputStream(reg.getInputStream());
            reg.destroy();

        } catch (IOException e1) {
            e1.printStackTrace();
            return "0";
        }
        if (fullStatus.length()==0){
            return "0";
        }else {
            return fullStatus;
        }
    }

    private static String getStringFromInputStream(InputStream inputStream) throws IOException {
        String newLine = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String line; (line = reader.readLine()) != null; ) {
            result.append(flag? newLine: "").append(line);
            flag = true;
        }
        return result.toString();
    }
}
