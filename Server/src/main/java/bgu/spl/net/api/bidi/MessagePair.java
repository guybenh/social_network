package bgu.spl.net.api.bidi;

import java.util.ArrayList;

public class MessagePair {

    private ArrayList privatePosts;
    private ArrayList publicPosts;

    public MessagePair(){
        this.privatePosts = new ArrayList();
        this.publicPosts = new ArrayList();
    }

    public ArrayList getPrivatePosts() {
        return privatePosts;
    }

    public void setPrivatePosts(ArrayList privatePosts) {
        this.privatePosts = privatePosts;
    }

    public ArrayList getPublicPosts() {
        return publicPosts;
    }

    public void setPublicPosts(ArrayList publicPosts) {
        this.publicPosts = publicPosts;
    }
}
