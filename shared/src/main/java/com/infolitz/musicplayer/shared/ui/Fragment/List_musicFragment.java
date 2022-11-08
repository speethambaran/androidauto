package com.infolitz.musicplayer.shared.ui.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.infolitz.musicplayer.shared.databinding.FragmentListMusicBinding;
import com.infolitz.musicplayer.shared.model.MusicModel;
import com.infolitz.musicplayer.shared.utils.RecyclerTouchListner;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.jahnen.libaums.core.UsbMassStorageDevice;


public class List_musicFragment extends Fragment {
    FragmentListMusicBinding binding;
    View view;
    MusicAdapter musicAdapter;
    List<MusicModel> musicModelList = new ArrayList<>();
    ActivityResultLauncher<String> storagePermissionLauncher;
    final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;


    ExoPlayer player;
    ActivityResultLauncher<String> recordAudioPermissionLauncher;
    final String recordAudioPermission = Manifest.permission.RECORD_AUDIO;
    String name;
    SetUpDevice setUpDevice;
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    UsbAccessory accessory;
    PendingIntent permissionIntent;

    UsbDeviceConnection connection = null;
    UsbMassStorageDevice device;

    private final byte[] bytes = new byte[64];
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListMusicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {

        binding.rlParentMusic.setVisibility(View.GONE);
        binding.rlRecycler.setVisibility(View.VISIBLE);
        binding.rlPlayed.setVisibility(View.GONE);

        usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

        permissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        getActivity().registerReceiver(usbReceiver, filter);
        usbManager();


        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                fetchSongs();
            } else {
                UserResponse();
            }
            checkSelfPermission();
        });
        storagePermissionLauncher.launch(permission);

        recordAudioPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        });

        player = new ExoPlayer.Builder(getActivity()).build();

        binding.rvMusicList.addOnItemTouchListener(new RecyclerTouchListner(getActivity(), binding.rvMusicList, new RecyclerTouchListner.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                if (!player.isPlaying()) {
                    player.setMediaItems(getMediaItems(), position, 0);
                } else {
                    player.pause();
                    player.seekTo(position, 0);
                }
                player.prepare();
                player.play();
//                binding.rlParentMusic.setVisibility(View.VISIBLE);
//                binding.rlRecycler.setVisibility(View.GONE);
                binding.rlPlayed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLongClick(View view, int position) {
                player.seekToNext();
            }
        }));


        binding.ivBack.setOnClickListener(view -> {
            binding.rlParentMusic.setVisibility(View.GONE);
            binding.rlRecycler.setVisibility(View.VISIBLE);
            binding.rlPlayed.setVisibility(View.VISIBLE);

        });
        binding.ivPlayList.setOnClickListener(view -> {
            binding.rlParentMusic.setVisibility(View.GONE);
            binding.rlRecycler.setVisibility(View.VISIBLE);
            binding.rlPlayed.setVisibility(View.VISIBLE);
        });
        binding.ivPause.setOnClickListener(view -> player.pause());
        binding.ivPlay.setOnClickListener(view -> player.play());
        binding.ivNext.setOnClickListener(view -> player.seekToNext());
        binding.ivPrevious.setOnClickListener(view -> player.seekToPrevious());

        binding.ivHomePause.setOnClickListener(view -> player.pause());
        binding.ivHomePlay.setOnClickListener(view -> player.play());
        binding.ivHomeNext.setOnClickListener(view -> player.seekToNext());
        binding.ivHomePrevious.setOnClickListener(view -> player.seekToPrevious());

        binding.ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rlRecycler.setVisibility(View.GONE);
                binding.rlParentMusic.setVisibility(View.VISIBLE);
                binding.rlPlayed.setVisibility(View.VISIBLE);
            }
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                binding.tvTitle.setText(mediaItem.mediaMetadata.title);
                binding.tvHomeSongName.setText(mediaItem.mediaMetadata.title);
                binding.sbMusic.setProgress((int) player.getCurrentPosition());
                binding.sbMusic.setMax((int) player.getDuration());
                binding.tvETime.setText(getReadableTime((int) player.getDuration()));
                if (!player.isPlaying()) {
                    player.play();
                }
                updatePlayerPositionProgress();
            }

            private void updatePlayerPositionProgress() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (player.isPlaying()) {
                            binding.tvSTime.setText(getReadableTime((int) player.getCurrentPosition()));
                            binding.sbMusic.setProgress((int) player.getCurrentPosition());
                        }
                        updatePlayerPositionProgress();
                    }
                }, 1000);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
            }
        });


    }


    private void fetchSongs() {
        List<MusicModel> musicModels = new ArrayList<>();
        Uri mediaStoreUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }


        String[] projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.ALBUM_ID,};
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED;
        try (Cursor cursor = getActivity().getContentResolver().query(mediaStoreUri, projection, null, null, null)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                long albumId = cursor.getLong(albumIdColumn);

                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                Uri albumArtWorkUri = ContentUris.withAppendedId(Uri.parse("content://media.external/audio/albumart"), albumId);
                name = name.substring(0, name.lastIndexOf("."));
                MusicModel musicModel = new MusicModel(name, uri, albumArtWorkUri, size, duration);
                musicModels.add(musicModel);

            }
            showSongs(musicModels);
        }

    }

    private void showSongs(List<MusicModel> musicModels1) {

        if (musicModels1.size() == 0) {
            Toast.makeText(getActivity(), "No songs", Toast.LENGTH_SHORT).show();
            return;
        }
        musicModelList.clear();
        musicModelList.addAll(musicModels1);


        musicAdapter = new MusicAdapter(getActivity(), musicModelList, player);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvMusicList.setAdapter(musicAdapter);
        binding.rvMusicList.setLayoutManager(linearLayoutManager);

    }


    private void UserResponse() {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            fetchSongs();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(permission)) {
                new AlertDialog.Builder(getActivity()).setTitle("Requesting permission").setMessage("Allow us to fetch songs on your device").setPositiveButton("allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        storagePermissionLauncher.launch(permission);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        } else {
            Toast.makeText(getActivity(), "Cancelled..", Toast.LENGTH_SHORT).show();
        }
    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (MusicModel musicModel : musicModelList) {
            MediaItem mediaItem = new MediaItem.Builder().setUri(musicModel.getUri()).setMediaMetadata(getMetaData(musicModel)).build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetaData(MusicModel musicModel) {
        return new MediaMetadata.Builder().setTitle(musicModel.getTitle()).setArtworkUri(musicModel.getArtworkUri()).build();
    }


    String getReadableTime(int duration) {
        String time;
        int hrs = duration / (1000 * 60 * 60);
        int min = (duration % (1000 * 60 * 60)) / (1000 * 60);
        int sec = (((duration % (1000 * 60 * 60)) % (1000 * 60 * 60)) % (1000 * 60)) / 1000;


        if (hrs < 1) {
            time = min + ":" + sec;
        } else {
            time = hrs + ":" + min + ":" + sec;
        }
        return time;
    }

    private void usbManager() {

        if (usbManager != null) {
            HashMap<String, UsbDevice> hostDevice = usbManager.getDeviceList();
            UsbDevice device = null;
            if (hostDevice != null) {
                Iterator<UsbDevice> deviceIterator = hostDevice.values().iterator();
                while (deviceIterator.hasNext()) {
                    device = deviceIterator.next();
                }
                Log.d("CDEVICE", "Host FOUND " + hostDevice);
                usbManager.requestPermission(device, permissionIntent);
                boolean hasPermision = usbManager.hasPermission(device);
                if (hasPermision) {
                    UsbDeviceConnection connection = usbManager.openDevice(device);
                    if (connection == null) {
                        return;
                    } else {
                        Log.d("CDEVICE", "Got connection ");
                    }
                }
            } else {
                Log.d("CDEVICE", "Host EMPTY");
            }
        }


/*        if (clef != null) {
            File directory = new File("/dev/bus/usb/001/"); //  /storage/UsbDriveA/    ....  /dev/bus/usb/001/006/
            Log.e("directory", String.valueOf(directory));
            if (directory != null) {

                Log.e("got into directory", String.valueOf(directory.canRead()));

                //.......
                if (directory.canRead()) {

                    File dir = new File("/dev/bus/usb/001/");
                    if (dir.exists() && dir.isDirectory()) {
                        // do something here
                        Log.e("g...", dir.exists() + " and " + dir.isDirectory());
//                        Uri.parse(new File("/dev/bus/usb/001/").toString());
                    }
                }

                //..............
            }
        }*/
    }


    final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("CDEVICE", "Action is : " + action);
            if (ACTION_USB_PERMISSION.equals(action)) {

                usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (usbDevice != null) {
                        //call method to set up accessory communication
                        Log.d("CDEVICE", "permission Accepted for device " + usbDevice.getManufacturerName());
                        connectUSB(usbDevice);

                    }

                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("CDEVICE", "USB device attached" + usbDevice.getManufacturerName());
                if (usbDevice != null) {

                }
            }
        }


    };


    private void connectUSB(UsbDevice device) {
        Log.d("CDEVICE", "Connect USb  : " + device.getManufacturerName());
        UsbInterface intf = device.getInterface(0);
        UsbEndpoint endpoint = intf.getEndpoint(0);

        int packetSize = endpoint.getMaxPacketSize();
        Log.d("CDEVICE", "Packet size  : " + packetSize);


        ByteBuffer buffer = ByteBuffer.allocate(packetSize);
        UsbDeviceConnection connection = usbManager.openDevice(device);
        UsbRequest usbRequest = new UsbRequest();

        usbRequest.initialize(connection, endpoint);
        usbRequest.queue(buffer, 1);

        if (connection.requestWait() == usbRequest) {

            UsbRequest inRequest = new UsbRequest();
            inRequest.initialize(connection, endpoint);

            if (inRequest.queue(buffer, packetSize) == true) {

                connection.requestWait();

                // get response data
                byte[] data = buffer.array();
                Log.d("CDEVICE", " byte Packet size  : " + data.toString());
            }

//            UsbDeviceConnection connection = usbManager.openDevice(device);

//        connection.claimInterface(intf, forceClaim);
//        connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT); //do in another thread

        }
    }


    private Boolean checkSelfPermission() {

        List<String> permissionsList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]), 1);
        }


        return true;
    }


}