package com.tryagain.com.fleetmanagmentsystem;

import android.view.MenuItem;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler
{
    public void write(String[] data, String fileName)
    {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath );
        CSVWriter writer = null;
        FileWriter mFileWriter = null;
// File exist
        if(f.exists() && !f.isDirectory()){
            try {
                mFileWriter = new FileWriter(filePath , true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);
        }
        else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writer.writeNext(data);

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // Toast.makeText(getApplicationContext(), "Records Saved",
                //Toast.LENGTH_LONG).show();
    }
}
