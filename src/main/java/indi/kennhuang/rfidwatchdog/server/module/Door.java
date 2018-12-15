package indi.kennhuang.rfidwatchdog.server.module;

import indi.kennhuang.rfidwatchdog.server.db.SQLite;
import indi.kennhuang.rfidwatchdog.server.module.permission.PermissionBlock;
import org.json.JSONArray;
import org.json.JSONObject;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Door {
    public int id;
    public String name;
    public String auth_token;
    public Map<Integer,PermissionBlock> permissionBlocks;

    public Door(){
        id = 0;
        name = "";
        auth_token = "";
        permissionBlocks = new HashMap<Integer, PermissionBlock>();
    }

    public static Door findDoorById(int id) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM doors where id is " + id);
        if(query.isClosed()){
            return null;
        }
        return putResult(query);
    }

    public static Door findDoorByName(String name) throws SQLException {
        ResultSet query = SQLite.getStatement().executeQuery("SELECT * FROM doors where name is '" + name +"'");
        if(query.isClosed()){
            return null;
        }
        return putResult(query);
    }
    // get

    public static void saveDoor(Door d) throws SQLException {
        if(findDoorById(d.id) == null){
            // new door
            SQLite.getStatement().execute("insert into doors (`name`, `auth_token`, `permission_blocks` ) values " +
                    "('"+d.name+"','"+d.auth_token+"','"+decodePermissionBlocks(d.permissionBlocks)+"')");
        }
        else{
            // old door
            SQLite.getStatement().execute("delete from doors where id is "+d.id);
            SQLite.getStatement().execute("insert into doors  (`id`, `name`, `auth_token`, `permission_blocks` ) values ("+d.id+",'"+d.name+"','"+d.auth_token+"','"+decodePermissionBlocks(d.permissionBlocks).toString()+"')");
        }
        SQLite.getConnection().commit();
    }

    public static void deleteDoor(Door d) throws SQLException {
        SQLite.getStatement().execute("delete from doors where id is "+d.id);
        SQLite.getConnection().commit();
    }

    private static Door putResult(ResultSet query) throws SQLException {
        Door res = new Door();
        res.id = query.getInt("id");
        res.name = query.getString("name");
        res.auth_token = query.getString("auth_token");
        res.permissionBlocks = encodePermissionBlocks(new JSONArray(query.getString("permission_blocks")));
        return res;
    }

    public static Map<Integer, PermissionBlock> encodePermissionBlocks(JSONArray json){
        Map<Integer,PermissionBlock> pbs = new HashMap<Integer, PermissionBlock>();

        for(int i=0;i<json.length();i++){
            JSONObject jsonObject = json.getJSONObject(i);
            PermissionBlock pb = PermissionBlock.encodePermissionBlock(jsonObject);
            pbs.put(pb.targetId,pb);
        }

        return pbs;
    }

    public static JSONArray decodePermissionBlocks(Map<Integer, PermissionBlock> pbs){
        JSONArray out = new JSONArray();

        Set ks = pbs.keySet();
        Iterator it = ks.iterator();
        while (it.hasNext()){
            PermissionBlock pb = pbs.get(it.next());
            out.put(PermissionBlock.decodePermissionBlock(pb));
        }
        return out;
    }
}
