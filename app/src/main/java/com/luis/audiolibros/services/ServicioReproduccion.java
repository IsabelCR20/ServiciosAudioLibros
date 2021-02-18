package com.luis.audiolibros.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.luis.audiolibros.DetalleFragment;
import com.luis.audiolibros.Libro;
import com.luis.audiolibros.MainActivity;
import com.luis.audiolibros.R;

import java.io.IOException;

public class ServicioReproduccion extends Service /*implements View.OnTouchListener, MediaPlayer.OnPreparedListener*/ {
    public static MediaPlayer mediaPlayer;
    private Notification notificacion;
    private static Uri audio;
    private static int indice;
    private IBinder binder;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DetalleFragment.mediaPlayer = mediaPlayer;
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d("cosa", "CREANDOSERVICIO: " );
        binder = new Binder();
        mediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int pos = 0;
        int idLibro = 0;
        if(intent.getExtras() != null && intent.getExtras().getInt("iniciado") != 0){
            pos = intent.getExtras().getInt("iniciado");
        }
        if(intent.getExtras() != null){
            idLibro =intent.getExtras().getInt("idxLibro");
        }
        Log.d("cosa", "StartCommand: "+audio);
        mediaPlayer = MediaPlayer.create(this, audio);
        if(pos != 0)
            mediaPlayer.seekTo(pos);
        //
        mediaPlayer.start();
        DetalleFragment.mediaPlayer = mediaPlayer;

        Intent notificationIntent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("libroIdx", indice);
        Log.d("cosa", "indice recibido en servicio: " + indice);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 20,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificacion = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icono)
                .setContentTitle("AudioLibros")
                .setContentText("Esta reproduciendo '" + Libro.ejemploLibros().elementAt(idLibro).titulo + "'")
                .setContentIntent(pendingIntent).build();
        startForeground(218, notificacion);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        stopForeground(true);
        super.onDestroy();
    }

    public static void setParametros(String sound, int idx){
        audio = Uri.parse(sound);
        indice = idx;
    }

    public class Binder extends android.os.Binder {
        public ServicioReproduccion getService() {
            return ServicioReproduccion.this;
        }
    }
}
   /*

    //Objetos necesarios
    public static MediaPlayer mediaPlayer;
    int indice;
    public static MediaController mediaController;
    Context contexto;
    private Notification notificacion;
    private boolean estado = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
             estado = intent.getExtras().getBoolean("iniciado");
            Log.d("cosa", "inciando servicio!!! ESTADO: " + estado);
            if (intent.getExtras().containsKey("uriAudio")) {
                // Obtener la informacion de los extras
                Uri audio = Uri.parse(intent.getExtras().getString("uriAudio"));
                this.indice = intent.getExtras().getInt("idxLibro", -3);
                //this.contexto = DetalleFragment.contexto;
                //DetalleFragment.vista_frag.setOnTouchListener(tocador);
                Log.d("cosa", "URI recibida:" + audio);
                Log.d("cosa", "INDICE recibida:" + indice);
                // Inicializar valores de objetos de reproduccion

                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(this);

                //mediaController = new MediaController(this.contexto);
                //Iniciar reproduccion
                try {
                    mediaPlayer.setDataSource(this, audio);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.d("cosa", "ERROR: No se puede reproducir " + audio);
                }


            }
        } else {
            Log.d("cosa", "intent null");
        }
        return START_STICKY;
    }

    View.OnTouchListener tocador = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("cosa", "Quiubo!!! TOUCH");
            //mediaController.show();
            return false;
        }
    };

    @Override
    public void onDestroy() {
        Log.d("cosa", "Me destruyeron :c");
        mediaPlayer.stop();
        stopForeground(true);
        super.onDestroy();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("cosa", "creando nitificacion");

    }

    // METODOS PROPIOS DEL MEDIAPLAYER Y




    // MEDIA CONTROLLER
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("cosa", "Entramos en onPrepared de MediaPlayer ESTADO: "+estado);
        mediaPlayer.start();

        //mediaController.setMediaPlayer(this);
        //mediaController.setAnchorView(DetalleFragment.vista_frag);
        //mediaController.setEnabled(true);
        //Log.d("cosa", "Detalle Fragment Vista: " + DetalleFragment.vista_frag);
        //mediaController.show();
        //
        Intent notificationIntent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("libroIdx", indice);
        Log.d("cosa", "indice recibido en servicio: " + indice);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 20,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         notificacion = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icono)
                .setContentTitle("AudioLibros")
                .setContentText("Esta reproduciendo")
                .setContentIntent(pendingIntent).build();

        notificacion.color = Color.YELLOW;
        startForeground(218, notificacion);

    }

/*

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mediaController.show();
        return false;
    }

    @Override
    public void start() {
        mediaPlayer.start();
        startForeground(218, notificacion);
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        stopForeground(true);
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


*/


//}
