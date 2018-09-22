package com.shuiyes.video.bean;

import java.util.ArrayList;

public class AlbumList extends ArrayList<Album>{

    @Override
    public boolean add(Album album) {
        boolean find = false;
        for (Album tmp:this){
            if(tmp.getPlayurl().equals(album.getPlayurl()) && tmp.getSize() == album.getSize()){
                find = true;
                break;
            }
        }
        if(!find){
            return super.add(album);
        }else{
            return false;
        }
    }
}
