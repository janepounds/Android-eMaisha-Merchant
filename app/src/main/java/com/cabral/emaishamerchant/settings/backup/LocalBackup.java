
package com.cabral.emaishamerchant.settings.backup;

import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.cabral.emaishamerchant.R;
import com.cabral.emaishamerchant.database.DatabaseOpenHelper;

import java.io.File;


public class LocalBackup {

    private BackupActivity activity;

    public LocalBackup(BackupActivity activity) {
        this.activity = activity;
    }

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    public void performBackup(final DatabaseOpenHelper db, final String outFileName) {



        File folder = new File(Environment.getExternalStorageDirectory() + File.separator +"SmartPos/" );

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            ViewGroup viewGroup = activity.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_backup_dialog, viewGroup, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            EditText input = dialogView.findViewById(R.id.dialog_backup);
            TextView backup_data = dialogView.findViewById(R.id.custom_add_backup);


            backup_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                            String m_Text = input.getText().toString();
                            String out = outFileName + m_Text ;
                            db.backup(out);
                            alertDialog.cancel();;

                }
            });

        } else
            Toast.makeText(activity, R.string.unable_to_create_directory_retry, Toast.LENGTH_SHORT).show();
    }

    //ask to the user what backup to restore
    public void performRestore(final DatabaseOpenHelper db) {


        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartPos/" );
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
            builderSingle.setTitle(R.string.database_restore);
            builderSingle.setNegativeButton(
                    R.string.cancel,
                    (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(
                    arrayAdapter,
                    (dialog, which) -> {
                        try {
                            db.importDB(files[which].getPath());
                        } catch (Exception e) {
                            Toast.makeText(activity, R.string.unable_to_restore_retry, Toast.LENGTH_SHORT).show();
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(activity, R.string.backup_folder_not_present, Toast.LENGTH_SHORT).show();
    }

}
