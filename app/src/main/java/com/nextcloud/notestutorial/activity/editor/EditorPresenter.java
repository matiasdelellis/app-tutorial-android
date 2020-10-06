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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.notestutorial.api.ApiProvider;
import com.nextcloud.notestutorial.model.Note;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditorPresenter {

    private EditorView view;

    public EditorPresenter(EditorView view) {
        this.view = view;
    }

    void createNote(Note note) {
        view.showProgress();

        Call<Note> call = ApiProvider.getAPI().create(note);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                ((AppCompatActivity) view).runOnUiThread(() -> {
                    view.hideProgress();
                    if (response.isSuccessful() && response.body() != null) {
                        view.onRequestSuccess(String.format("Save new note: %s", response.body().getTitle()));
                    } else {
                        view.onRequestError("Error");
                    }
                });
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                ((AppCompatActivity) view).runOnUiThread(() -> {
                    view.hideProgress();
                    view.onRequestError(t.getLocalizedMessage());
                });
            }
        });
    }

    void updateNote(Note note) {
        view.showProgress();

        Call<Note> call = ApiProvider.getAPI().updateNote(note.getId(), note);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                ((AppCompatActivity) view).runOnUiThread(() -> {
                    view.hideProgress();

                    if (response.isSuccessful() && response.body() != null) {
                        view.onRequestSuccess(String.format("Note '%s' saved", response.body().getTitle()));
                    } else {
                        view.onRequestError("Error");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                ((AppCompatActivity) view).runOnUiThread(() -> {
                    view.hideProgress();
                    view.onRequestError(t.getLocalizedMessage());
                });
            }
        });

    }

    void deleteNote(int id) {
        view.showProgress();

        Call<Note> call = ApiProvider.getAPI().deleteNote(id);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                ((AppCompatActivity) view).runOnUiThread(() -> {
                    view.hideProgress();
                    view.onRequestSuccess("Note deleted...");
                });
            }
            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                ((AppCompatActivity) view).runOnUiThread(() -> {
                    view.hideProgress();
                    view.onRequestError(t.getLocalizedMessage());
                });
            }
        });

    }
}
