package com.luis.audiolibros.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.luis.audiolibros.DetalleFragment;
import com.luis.audiolibros.R;

import java.io.IOException;

public class ServicioReproduccion extends Service implements View.OnTouchListener, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl{
    //Objetos necesarios
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    Context contexto;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("cosa", "inciando servicio!!!");
        if(intent.getExtras().containsKey("uriAudio")){
            // Obtener la informacion de los extras
            Uri audio = Uri.parse(intent.getExtras().getString("uriAudio"));
            this.contexto = DetalleFragment.contexto;


            Log.d("cosa", "URI recibida:" + audio);
            // Inicializar valores de objetos de reproduccion
            if (mediaPlayer != null){
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaController = new MediaController(this.contexto);
            //Iniciar reproduccion
            try {
                mediaPlayer.setDataSource(contexto, audio);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Log.d("cosa", "ERROR: No se puede reproducir "+audio);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // METODOS PROPIOS DEL MEDIAPLAYER Y




    // MEDIA CONTROLLER
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("cosa", "Entramos en onPrepared de MediaPlayer");
        //Obtenermos la vista
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_detalle, null);

        //
        mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(DetalleFragment.vista_frag);
        mediaController.setEnabled(true);
        mediaController.show();
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mediaController.show();
        return false;
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }




}
