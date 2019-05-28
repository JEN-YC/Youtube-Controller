package com.miller.phone_controll;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connected_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ClientClass clientClass;
    ServerClass serverClass;
    public static WriteClass writeClass;
    ListView searchListView;
    List<VideoPojo> searchResults;
    YoutubeAPI youtubeAPI;
    Handler handler;
    YoutubeAdapter searchAdapter;
    private static Toast toast;
    public static FavoriteAdapter favoriteAdapter;
    TextView curr_video_list;
    public static List<VideoPojo> favorite_list;
    static final String ORDER = "1";


    static final String CHINESE_SONG = "PLDAAE8FCD772FB4BA";
    static final String ENGLISH_SONG = "PLA5F0EC50C3B8D29B";
    static final String CHINESE_RANK = "PLsyOSbh5bs15OXJIigNdRgK0za-JXwhz1";
    static final String ENGLISH_RANK = "PLx0sYbCqOb8TBPRdmBHs5Iftvv9TPboYG";
    static final String CHINESE_NEW = "PLsyOSbh5bs16vubvKePAQ1x3PhKavfBIl";
    static final String ENGLISH_NEW = "PLw-VjHDlEOgtCjYJB1r1EkZ-AKlYv6Ozj";
    final mfragment fragment = new mfragment();
    public static Favorite_Database favorite_database;
    Boolean isfragment = false;

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if (writeClass != null){
                writeClass = null;
            }
            if(serverClass != null){
                makeTextAndShow(getApplicationContext(), "關閉server", 1000);
                serverClass.socket.close();
                serverClass = null;
            }
            if(clientClass != null){
                makeTextAndShow(getApplicationContext(), "關閉client", 1000);
                clientClass.socket.close();
                clientClass = null;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_page);
        initWorks();
        processViews();
        processControllers();


    }

    private void processViews() {
        favorite_database = new Favorite_Database(getApplicationContext());
        youtubeAPI = new YoutubeAPI(Connected_page.this);
        handler = new Handler();
        searchListView = findViewById(R.id.video_list);
        curr_video_list = findViewById(R.id.curr_video);
    }

    private static void makeTextAndShow(final Context context, final String text, final int duration) {
        if (toast == null) {
            //如果還沒有用過makeText方法，才使用
            toast = android.widget.Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }
    private void processControllers() {
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoPojo video = searchResults.get(position);
                final String name = video.getTitle();
                final String video_id = video.getId();
                final String description = video.getDescription();
                final String URL = video.getThumbnailURL();
                AlertDialog.Builder builder = new AlertDialog.Builder(Connected_page.this);
                builder.setMessage("確定要添加「" + name + "」至歌單中嗎?")
                        .setTitle("點歌確認");
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Map<String, String> map = new HashMap<>();
                            map.put("ID", video_id);
                            map.put("Title", name);
                            map.put("URL", URL);
                            map.put("description", description);
                            map.put("command", ORDER);
                            JSONObject json = new JSONObject(map);
                            String jsonString = json.toString();
                            writeClass.write_msg(jsonString.getBytes());
                        }catch (NullPointerException e){
                            makeTextAndShow(getApplicationContext(), "已與投影機斷線，請回主頁重新連線", Toast.LENGTH_SHORT);
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void initWorks() {
        if(MainActivity.isHost) {
            serverClass = new ServerClass();
            serverClass.start();
        }
        else{
            clientClass = new ClientClass(MainActivity.hostAddress);
            clientClass.start();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment);
        fragmentTransaction.hide(fragment).commit();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#ff6423"));
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.mipmap.app_icon_round));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 開啟遙控器fragment
                if (isfragment) {
                    hideFragment(fragment);
                } else {
                    showFragment(fragment);
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Connected_page.this, MainActivity.class);
            startActivity(intent);
            Connected_page.this.finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connected_page, menu);
        MenuItem menuSearchItem = menu.findItem(R.id.search_bar);

        final SearchView searchView = (SearchView) menuSearchItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (s.equals("")) {
                } else {
                    new Thread() {
                        public void run() {
                            searchResults = youtubeAPI.search(s);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (searchResults.size() > 0) {
                                        hideFragment(fragment);
                                        searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                        searchListView.setAdapter(searchAdapter);
                                        curr_video_list.setText(s);
                                    }
                                }
                            });
                        }
                    }.start();
                }
                searchView.clearFocus();
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.chinese_song) {
            new Thread() {
                public void run() {
                    searchResults = youtubeAPI.get_items(CHINESE_SONG);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchResults.size() > 0) {
                                hideFragment(fragment);
                                searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                searchListView.setAdapter(searchAdapter);
                                curr_video_list.setText(R.string.chinese_song);
                            }
                        }
                    });
                }
            }.start();
        } else if (id == R.id.english_song) {
            new Thread() {
                public void run() {
                    searchResults = youtubeAPI.get_items(ENGLISH_SONG);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchResults.size() > 0) {
                                hideFragment(fragment);
                                searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                searchListView.setAdapter(searchAdapter);
                                curr_video_list.setText(R.string.english_song);
                            }
                        }
                    });
                }
            }.start();
        } else if (id == R.id.chinese_rank) {
            new Thread() {
                public void run() {
                    searchResults = youtubeAPI.get_items(CHINESE_RANK);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchResults.size() > 0) {
                                hideFragment(fragment);
                                searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                searchListView.setAdapter(searchAdapter);
                                curr_video_list.setText(R.string.chinese_rank);
                            }
                        }
                    });
                }
            }.start();
        } else if (id == R.id.english_rank) {
            new Thread() {
                public void run() {
                    searchResults = youtubeAPI.get_items(ENGLISH_RANK);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchResults.size() > 0) {
                                hideFragment(fragment);
                                searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                searchListView.setAdapter(searchAdapter);
                                curr_video_list.setText(R.string.english_rank);
                            }
                        }
                    });
                }
            }.start();
        } else if (id == R.id.chinese_new) {
            new Thread() {
                public void run() {
                    searchResults = youtubeAPI.get_items(CHINESE_NEW);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchResults.size() > 0) {
                                hideFragment(fragment);
                                searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                searchListView.setAdapter(searchAdapter);
                                curr_video_list.setText(R.string.chinese_new);
                            }
                        }
                    });
                }
            }.start();
        } else if (id == R.id.english_new) {
            new Thread() {
                public void run() {
                    searchResults = youtubeAPI.get_items(ENGLISH_NEW);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchResults.size() > 0) {
                                hideFragment(fragment);
                                searchAdapter = new YoutubeAdapter(Connected_page.this, searchResults);
                                searchListView.setAdapter(searchAdapter);
                                curr_video_list.setText(R.string.english_new);
                            }
                        }
                    });
                }
            }.start();
        } else if (id == R.id.favorite) {
            searchResults = favorite_database.getAll();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (searchResults.size() > 0) {
                        hideFragment(fragment);
                        favoriteAdapter = new FavoriteAdapter(Connected_page.this, searchResults);
                        searchListView.setAdapter(favoriteAdapter);
                        curr_video_list.setText(R.string.favorite);
                    }
                    else{
                        makeTextAndShow(getApplicationContext(), "我的最愛目前沒有歌曲喔", Toast.LENGTH_SHORT);
                    }
                }
            });
        }


//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(final mfragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(fragment).commit();
        isfragment = true;
        searchListView.setVisibility(View.INVISIBLE);
        curr_video_list.setVisibility(View.INVISIBLE);
    }

    private void hideFragment(final mfragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragment).commit();
        isfragment = false;
        searchListView.setVisibility(View.VISIBLE);
        curr_video_list.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.mManager.removeGroup(MainActivity.mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                makeTextAndShow(getApplicationContext(), "已斷開連線", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    public class WriteClass extends Thread {
        private Socket socket;
        private DataOutputStream outputStream;

        public WriteClass(Socket socket) {
            this.socket = socket;
            try {
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void write_msg(final byte[] bytes) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAddr;

        public ClientClass(InetAddress hostAddress) {
            hostAddr = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddr, 8889), 500);
                writeClass = new WriteClass(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(8889));
                while(true) {
                    socket = serverSocket.accept();
                    writeClass = new WriteClass(socket);
                }
            } catch (IOException e) {
                Log.e("error", e.toString());
                e.printStackTrace();
            }
        }
    }
}
