/*
 * Nextcloud Notes Tutorial for Android
 *
 * @copyright Copyright (c) 2020 John Doe <john@doe.com>
 * @author John Doe <john@doe.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextcloud.notestutorial.activity.editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Objects;

import com.nextcloud.notestutorial.R;
import com.nextcloud.notestutorial.api.ApiProvider;
import com.nextcloud.notestutorial.model.Note;
import com.nextcloud.notestutorial.util.ColorUtil;

public class EditorActivity extends AppCompatActivity implements EditorView {

    EditorPresenter presenter;
    ProgressDialog progressDialog;

    protected ApiProvider mApi;

    EditText et_title;
    EditText et_content;

    Note note = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

            Drawable drawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_back_grey, null);
            if (drawable != null) {
                DrawableCompat.setTint(drawable, getResources().getColor(R.color.defaultNoteTint));
                actionBar.setHomeAsUpIndicator(drawable);
            } else {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
            }
        }

        mApi = new ApiProvider(getApplicationContext());

        et_title = findViewById(R.id.title);
        et_content = findViewById(R.id.content);

        // Create progress dialog.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        presenter = new EditorPresenter(this);

        Intent intent = getIntent();
        if (intent.hasExtra("note")) {
            note = (Note) Objects.requireNonNull(intent.getSerializableExtra("note"));
        }

        setDataFromIntentExtra();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int tintColor = this.getResources().getColor(R.color.defaultNoteTint);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);

        MenuItem deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(note.getId() != 0);
        ColorUtil.menuItemTintColor(deleteItem, tintColor);

        ColorUtil.menuItemTintColor(menu.findItem(R.id.save), tintColor);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                // Update note from view.
                note.setTitle(et_title.getText().toString().trim());
                note.setContent(et_content.getText().toString().trim());

                if (note.getTitle().isEmpty()) {
                    et_title.setError(getString(R.string.enter_title));
                } else if (note.getContent().isEmpty()) {
                    et_content.setError(getString(R.string.enter_note));
                } else {
                    if (note.getId() == 0)
                        presenter.createNote(note);
                    else
                        presenter.updateNote(note);
                }
                return true;
            case R.id.delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.delete_note));
                alertDialog.setMessage(R.string.confirm_delete);
                alertDialog.setNegativeButton(R.string.common_yes, (dialog, wich) -> {
                    dialog.dismiss();
                    presenter.deleteNote(note.getId());
                });
                alertDialog.setPositiveButton(R.string.common_cancel, ((dialog, which) -> dialog.dismiss()));
                alertDialog.show();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void onRequestSuccess(String message) {
        Toast.makeText(EditorActivity.this, message, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRequestError(String message) {
        Toast.makeText(EditorActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void setDataFromIntentExtra() {
        if (note.getId() != 0) {
            et_title.setText(note.getTitle().trim());
            et_content.setText(note.getContent().trim());
        }
         // Default color.
        int defaultColor = getResources().getColor(R.color.defaultNoteColor);
        et_content.getRootView().setBackgroundColor(defaultColor);
        // Focus to title and edit
        et_title.requestFocus();
        editMode();
    }

    private void editMode() {
        et_title.setFocusableInTouchMode(true);
        et_content.setFocusableInTouchMode(true);
    }

    private void readMode() {
        et_title.setFocusableInTouchMode(false);
        et_content.setFocusableInTouchMode(false);
        et_title.setFocusable(false);
        et_content.setFocusable(false);
    }

    private void closeEdition () {
        setResult(RESULT_CANCELED);
        finish();
    }
}