package client.loadBalancer;


import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;


public interface LoadBalancer {

    Instance getInstance(List<Instance> list);

}
