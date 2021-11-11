package com.example.openvpn.storage;

import android.content.Context;
import android.content.SharedPreferences;
import static com.example.openvpn.utils.Utils.getImgURL;
import com.example.openvpn.R;
import com.example.openvpn.model.Server;

public class SharedPreference {

    private static final String APP_PREFS_NAME = "OpenVPNPreference";

    private final SharedPreferences mPreference;
    private final SharedPreferences.Editor mPrefEditor;

    private static final String SERVER_COUNTRY = "server_country";
    private static final String SERVER_FLAG = "server_flag";
    private static final String SERVER_OVPN = "server_ovpn";
    private static final String SERVER_OVPN_USER = "server_ovpn_user";
    private static final String SERVER_OVPN_PASSWORD = "server_ovpn_password";

    public SharedPreference(Context context) {
        this.mPreference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = mPreference.edit();
    }

    /**
     * Save server details
     * @param server details of ovpn server
     */
    public void saveServer(Server server){
        mPrefEditor.putString(SERVER_COUNTRY, server.getCountryName());
        mPrefEditor.putString(SERVER_FLAG, server.getFlagUrl());
        mPrefEditor.putString(SERVER_OVPN, server.getToVPN());
        mPrefEditor.putString(SERVER_OVPN_USER, server.getToVPNUserName());
        mPrefEditor.putString(SERVER_OVPN_PASSWORD, server.getToVPNUserPassword());
        mPrefEditor.commit();
    }

    /**
     * Get server data from shared preference
     * @return server model object
     */
    public Server getServer() {

        Server server = new Server(
                mPreference.getString(SERVER_COUNTRY,"Japan"),
                mPreference.getString(SERVER_FLAG,getImgURL(R.drawable.japan)),
                mPreference.getString(SERVER_OVPN,"japan.ovpn"),
                mPreference.getString(SERVER_OVPN_USER,"vpn"),
                mPreference.getString(SERVER_OVPN_PASSWORD,"vpn")
        );

        return server;
    }
}
