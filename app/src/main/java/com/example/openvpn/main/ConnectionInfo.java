package com.example.openvpn.main;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.openvpn.network.NetworkStatus;
import com.example.openvpn.R;
import com.example.openvpn.storage.SharedPreference;
import com.example.openvpn.databinding.FragmentMainBinding;
import com.example.openvpn.interfaces.SwitchServer;
import com.example.openvpn.model.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

public class ConnectionInfo extends Fragment implements View.OnClickListener, SwitchServer {

    private Server server;
    private NetworkStatus connection;
    private final OpenVPNThread vpnThread = new OpenVPNThread();
    private final OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;
    private FragmentMainBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View view = binding.getRoot();
        // initialize all variables
        initializeAll();

        return view;
    }

    private void initializeAll() {
        preference = new SharedPreference(getContext());
        server = preference.getServer();

        // Updating the location icon according to selected server
        updateCurrentServerIcon(server.getFlagUrl());
        connection = new NetworkStatus();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.vpnBtn.setOnClickListener(this);

        // Checking if vpn is already running or not
        isServiceRunning();
        VpnStatus.initLogCache(getActivity().getCacheDir());
    }

    @Override
    public void onClick(View v) {
        // Vpn is running, user would like to disconnect current connection.
        if (v.getId() == R.id.vpnBtn) {
            if (vpnStart) {
                confirmDisconnect();
            } else {
                prepareVpn();
            }
        }
    }

    // Show disconnect dialogue (YES/NO)
    public void confirmDisconnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.connection_close_confirm));

        builder.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Prepare the VPN for connecting with required permission
    private void prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {

                // Checking permission for network monitor
                Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();// Already have got permission

                // Update the connection status
                status("connecting");

            } else {

                // No internet connection available
                showToast("you have no internet connection !!");
            }

        } else if (stopVpn()) {

            // VPN is stopped, show a Toast message.
            showToast("Disconnect Successfully");
        }
    }

    // Stop VPN
    public boolean stopVpn() {
        try {
            vpnThread.stop();

            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Taking permission for Network Access
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Permission granted, start the VPN
            startVpn();
        } else {
            showToast("Permission Deny !! ");
        }
    }

    // Internet connection status
    public boolean getInternetStatus() {
        return connection.netCheck(getContext());
    }

    // Get service status
    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }

    // Start the VPN
    @SuppressLint("SetTextI18n")
    private void startVpn() {
        try {
            // .ovpn file
            InputStream conf = getActivity().getAssets().open(server.getToVPN());
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }

            br.readLine();
            OpenVpnApi.startVpn(getContext(), config, server.getCountryName(), server.getToVPNUserName(), server.getToVPNUserPassword());

            // Update log
            binding.logTv.setText("Connecting...");
            vpnStart = true;

        } catch (IOException | RemoteException e) {
            e.printStackTrace();
        }
    }

    // Status change with corresponding VPN connection status
    @SuppressLint("SetTextI18n")
    public void setStatus(String connectionState) {
        if (connectionState != null)
            switch (connectionState) {
                case "DISCONNECTED":
                    status("connect");
                    vpnStart = false;
                    vpnService.setDefaultStatus();
                    binding.logTv.setText("");
                    break;
                case "CONNECTED":
                    vpnStart = true;// it will use after restart this activity
                    status("connected");
                    binding.logTv.setText("");
                    break;
                case "WAIT":
                    binding.logTv.setText("waiting for server connection!!");
                    break;
                case "AUTH":
                    binding.logTv.setText("server authenticating!!");
                    break;
                case "RECONNECTING":
                    status("connecting");
                    binding.logTv.setText("Reconnecting...");
                    break;
                case "NONETWORK":
                    binding.logTv.setText("No network connection");
                    break;
            }

    }

    // Change button background color and text
    @SuppressLint("SetTextI18n")
    public void status(String status) {

        switch (status) {
            case "connect":
                binding.vpnBtn.setText(getContext().getString(R.string.connect));
                break;
            case "connecting":
                binding.vpnBtn.setText(getContext().getString(R.string.connecting));
                break;
            case "connected":

                binding.vpnBtn.setText(getContext().getString(R.string.disconnect));

                break;
            case "tryDifferentServer":

                binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
                binding.vpnBtn.setText("Try Different\nServer");
                break;
            case "loading":
                binding.vpnBtn.setBackgroundResource(R.drawable.button);
                binding.vpnBtn.setText("Loading Server..");
                break;
            case "invalidDevice":
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
                binding.vpnBtn.setText("Invalid Device");
                break;
            case "authenticationCheck":
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting);
                binding.vpnBtn.setText("Authentication \n Checking...");
                break;
        }

    }

    // Recieve broadcast message
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Update status UI
     *
     * @param duration:          running time
     * @param lastPacketReceive: last packet receive time
     * @param byteIn:            incoming data
     * @param byteOut:           outgoing data
     */
    @SuppressLint("SetTextI18n")
    public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
        binding.durationTv.setText("Duration: " + duration);
        binding.lastPacketReceiveTv.setText("Packet Received: " + lastPacketReceive + " second ago");
        binding.byteInTv.setText("Bytes In: " + byteIn);
        binding.byteOutTv.setText("Bytes Out: " + byteOut);
    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void updateCurrentServerIcon(String serverIcon) {
        Glide.with(getContext())
                .load(serverIcon)
                .into(binding.selectedServerIcon);
    }

    // Change server when select the new server
    @Override
    public void newServer(Server server) {
        this.server = server;
        updateCurrentServerIcon(server.getFlagUrl());

        // Stop previous connection
        if (vpnStart) {
            stopVpn();
        }

        prepareVpn();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        if (server == null) {
            server = preference.getServer();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    // Save current selected server on local shared preferences storage
    @Override
    public void onStop() {
        if (server != null) {
            preference.saveServer(server);
        }

        super.onStop();
    }
}