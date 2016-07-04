package com.akbros.musicmatch;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SOUNDCLOUD";
    private static List<Track> mListItems = null;
    private static List<Message> messageListItems = null;
    private SCTrackAdapter mAdapter = null;

    private MessagesAdapter messagesAdapter;
    static Track playing = null;

    private static boolean chatting = false;

    MainActivity mainActivity = this;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private ImageButton searchButton;
    private EditText searchEditText;
    private TextView mSelectedTrackTitle;
    private ImageView mSelectedTrackImage;
    private static MediaPlayer mMediaPlayer = null;
    private ImageView mPlayerControl;
    private Button startChatButton;
    private EditText messageEditText;
    private Button messageSendButton;
    private Server server = null;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        searchButton = (ImageButton)findViewById(R.id.searchButton);
        searchEditText = (EditText)findViewById(R.id.searchEditText);
        mSelectedTrackTitle = (TextView)findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView)findViewById(R.id.selected_track_image);
        mPlayerControl = (ImageView)findViewById(R.id.player_control);
        startChatButton = (Button)findViewById(R.id.startChat_button);
        messageSendButton = (Button)findViewById(R.id.message_send);
        messageEditText = (EditText)findViewById(R.id.message_editText);

        if(server==null){
            server = new Server();
            server.connect();
            server.getSocket().on("clientConnected", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messagesAdapter.addMessage(Message.TYPE_COMMENT,"New stranger connected to chat listening to the same music!!");
                        }
                    });
                }
            });

            server.getSocket().on("clientDisconnected", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messagesAdapter.addMessage(Message.TYPE_COMMENT,"Stranger left the chat");
                            startChatButton.setText("Start new chat");
                            chatting = false;
                        }
                    });
                }
            });

            server.getSocket().on("message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message = args[0].toString();
                            messagesAdapter.addMessage(Message.TYPE_STRANGER,message);
                        }
                    });
                }
            });
        }

        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"send message button clicked");
                String message = messageEditText.getText().toString();
                if(message=="")return;
                server.sendMessage(message);
                messagesAdapter.addMessage(Message.TYPE_YOU,message);
                messageEditText.setText("");
            }
        });

        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"start chat button clicked!!");
                if(chatting){
                    server.stopChat();
                    messagesAdapter.addMessage(Message.TYPE_COMMENT,"Chat closed by you");
                    startChatButton.setText("Start new Chat");
                }else{
                    if(playing==null)return;
                    server.startChat(playing.getID()+"");
                    messagesAdapter.clearChat();
                    messagesAdapter.addMessage(Message.TYPE_COMMENT,"Waiting for stranger");
                    startChatButton.setText("Stop Chat");
                }
                chatting = !chatting;
            }
        });



        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        if(mMediaPlayer==null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    togglePlayPause();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayerControl.setImageResource(R.drawable.ic_play);
                    server.stopSong();
                }
            });
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCService scService = SoundCloud.getService();
                scService.searchTracks(searchEditText.getText().toString(),new Callback<List<Track>>() {
                    @Override
                    public void success(List<Track> tracks, Response response) {
                        Log.d(TAG,"URL ::: "+response.getUrl()+"");
                        Log.d(TAG,tracks.size()+"");
                        loadTracks(tracks);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Error: " + error);
                    }
                });
            }
        });

        if(mListItems==null){
            mListItems = new ArrayList<Track>();
        }
        ListView listView = (ListView)findViewById(R.id.track_list_view);
        mAdapter = new SCTrackAdapter(this, mListItems);
        listView.setAdapter(mAdapter);

        if(messageListItems==null){
            messageListItems = new ArrayList<Message>();
        }
        ListView messagesListView = (ListView)findViewById(R.id.messages_list);
        messagesAdapter = new MessagesAdapter(this,messageListItems);
        messagesListView.setAdapter(messagesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = mListItems.get(position);

                mSelectedTrackTitle.setText(track.getTitle());
                Picasso.with(MainActivity.this).load(track.getArtworkURL()).into(mSelectedTrackImage);

                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mPlayerControl.setImageResource(R.drawable.ic_play);
                try {
                    mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                    mMediaPlayer.prepareAsync();
                    if(playing!=null)server.stopSong();
                    server.startSong(Integer.toString(track.getID()));
                    playing = track;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTracks(List<Track> tracks) {
        mListItems.clear();
        mListItems.addAll(tracks);
        mAdapter.notifyDataSetChanged();
    }

    private void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.ic_play);
        } else {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause);
        }
    }
}
