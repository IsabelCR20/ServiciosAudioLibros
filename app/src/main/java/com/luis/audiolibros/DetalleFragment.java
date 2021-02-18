package com.luis.audiolibros;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.luis.audiolibros.services.ServicioReproduccion;

import java.io.IOException;
import java.io.Serializable;

public class DetalleFragment extends Fragment implements View.OnTouchListener, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl{
    public static final String ARG_ID_LIBRO = "id_libro";
    //public static View vista_frag;
    //public static Context contexto;
    public static MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Intent intentServicio;
    private Libro libro;
    private int idLibro;
    boolean iniciado = false;
    private Intent servicioRep;
    private ServicioReproduccion sr;
    private boolean isConnected;
    private int currentTime;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_detalle, container, false);
        //vista_frag = vista.findViewById(R.id.fragment_detalle);
        //contexto = getActivity();
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_ID_LIBRO)) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        mediaController = new MediaController(getActivity());
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(vista.findViewById(R.id.fragment_detalle));
        mediaController.setEnabled(true);
        mediaPlayer = new MediaPlayer();
        Log.d("cosa", "creando vista");
        //mediaPlayer = sr.mediaPlayer;
        return vista;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ponInfoLibro(int id){
        ponInfoLibro(id, getView());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ponInfoLibro(int id, View vista) {

        //intentServicio = new Intent(getContext(), MiServicio.class);
        //getActivity().startService(intentServicio);

        //intentServicio = new Intent(getContext(), MiIntentService.class);
        //getActivity().startService(intentServicio);

        libro = Libro.ejemploLibros().elementAt(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((ImageView) vista.findViewById(R.id.portada)).setImageResource(libro.recursoImagen);

        vista.setOnTouchListener(this);
        /*
        if (mediaPlayer != null){
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
         */
        Uri audio = Uri.parse(libro.urlAudio);
        Bundle args = getArguments();
        if(args != null && args.containsKey("iniciado")){
            Log.d("cosa", "INICIADO: " + args.getInt("iniciado"));
            if(args.getInt("iniciado") == 10){
                iniciado = true;
                Log.d("cosa", "DIEZ 101010");
            }
        }
        Log.d("cosa", "URI enviada:" + libro.urlAudio);
        Log.d("cosa", "Indice enviado:" + id);

        Log.d("cosa", "estado enviado:" + iniciado);
        this.idLibro = id;
        sr = new ServicioReproduccion();
        if(!iniciado) {

            sr.setParametros(libro.urlAudio, id);
            //Llamar al servicio señalando la uri
            servicioRep = new Intent(getActivity().getBaseContext(), sr.getClass());
            servicioRep.putExtra("uriAudio", libro.urlAudio);
            servicioRep.putExtra("idxLibro", id);
            servicioRep.putExtra("iniciado", 0);
            Log.d("cosa", "Listo para inciar servicio cosas: " );
            getActivity().startService(servicioRep);

            //mediaPlayer = sr.mediaPlayer;
        } else {
            servicioRep = new Intent(getActivity().getBaseContext(), sr.getClass());
            getActivity().bindService(servicioRep, connection, Context.BIND_AUTO_CREATE);
            Log.d("cosa", "ELSE: " + sr.mediaPlayer);
            //mediaPlayer = sr.mediaPlayer;

        }

        /*
        try {
            mediaPlayer.setDataSource(getActivity(), audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir "+audio,e);
        }
         */
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("cosa", "Hola");
        mediaController.show();
        return false;
    }



    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
    }


    @Override
    public void start() {
        sr.setParametros(libro.urlAudio, idLibro);
        servicioRep.putExtra("uriAudio", libro.urlAudio);
        servicioRep.putExtra("idxLibro", idLibro);
        servicioRep.putExtra("iniciado", currentTime);

        Log.d("cosa", "Listo para inciar servicio");
        getActivity().startService(servicioRep);

        mediaController.show();
    }

    @Override
    public void pause() {
        Log.d("cosa","Hola PAUSE");
        currentTime = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
        if(!iniciado)
            getActivity().stopService(servicioRep);
        else
            sr.onDestroy();


    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        Log.d("cosa", "POS"+mediaPlayer.getCurrentPosition()+"");
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


    @Override
    public void onStop(){
        Log.d("cosa","Hola STOP");
        mediaController.hide();
        mediaPlayer.stop();
        if(!iniciado)
            getActivity().stopService(servicioRep);
        else
            sr.onDestroy();
        super.onStop();
    }



    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ServicioReproduccion.Binder binder = (ServicioReproduccion.Binder) service;
            sr = binder.getService();
            sr.onBind(servicioRep);
            isConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isConnected = false;
        }
    };
}