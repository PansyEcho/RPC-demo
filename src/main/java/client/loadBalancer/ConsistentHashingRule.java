package client.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class ConsistentHashingRule implements LoadBalancer{
    @Override
    public Instance getInstance(List<Instance> list) {
        ConsistentHash consistentHash = new ConsistentHash(list);
        try {
            consistentHash.getServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws UnknownHostException {
        Instance instance1 = new Instance();
        instance1.setIp("44.123.244.99");
        Instance instance2 = new Instance();
        instance2.setIp("56.22.133.42");
        Instance instance3 = new Instance();
        instance3.setIp("137.32.43.123");
        Instance instance4 = new Instance();
        instance4.setIp("87.133.23.144");
        ArrayList<Instance> list = new ArrayList<>();
        list.add(instance1);
        list.add(instance2);
        list.add(instance3);
        list.add(instance4);
        ConsistentHash hash = new ConsistentHash(list, 120);
        for (int i = 0; i < 10; i++){
            String server = hash.getServers((i * 213.23) + "");
            System.out.println(i + ":" + server);
        }
    }

}

class ConsistentHash {
    private static TreeMap<Integer,String> Nodes = new TreeMap();
    private static int VIRTUAL_NODES = 160;
    private static List<Instance> instances = new ArrayList<>();

    public ConsistentHash(List<Instance> instances, int VIRTUAL_NODES){
        this.instances = instances;
        this.VIRTUAL_NODES = VIRTUAL_NODES;
        init();
    }
    public ConsistentHash(List<Instance> instances){
        this.instances = instances;
        init();
    }

    public static HashMap<String,Instance> map = new HashMap<>();//将服务实例与ip地址一一映射
    //预处理 形成哈希环
    public static void init(){
        for (Instance instance : instances) {
            String ip = instance.getIp();
            Nodes.put(getHash(ip),ip);
            map.put(ip,instance);
            for(int i = 0; i < VIRTUAL_NODES; i++) {
                int hash = getHash(ip+"#"+i);
                Nodes.put(hash,ip);
            }
        }
    }
    public String getServer() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        int hash = getHash(hostAddress);
        //得到大于该Hash值的子红黑树
        SortedMap<Integer,String> subMap = Nodes.tailMap(hash);
        if (subMap.isEmpty()){
            return Nodes.get(Nodes.firstKey());
        }
        //获取该子树最小元素
        Integer nodeIndex = subMap.firstKey();
        return Nodes.get(nodeIndex);
    }

    public  String getServers(String clientInfo) {
        int hash = getHash(clientInfo);
        //得到大于该Hash值的子红黑树
        SortedMap<Integer,String> subMap = Nodes.tailMap(hash);
        if (subMap.isEmpty()){
            return Nodes.get(Nodes.firstKey());
        }
        //获取该子树最小元素
        Integer nodeIndex = subMap.firstKey();
        return Nodes.get(nodeIndex);
    }
//    用hashcode()效果一样，只是FNV1_32_HASH算法更好
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash^str.charAt(i))*p;
            hash +=hash <<13;
            hash ^=hash >>7;
            hash +=hash <<3;
            hash ^=hash >>17;
            hash +=hash <<5;
            //如果算出来的值为负数 取其绝对值
            if(hash < 0) {
                hash = Math.abs(hash);
            }
        }
        return hash;
    }

}










