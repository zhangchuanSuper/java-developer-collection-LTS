## 一、DockerDesktop安装ES

1、运行Docker命令安装ES8

① 使用Docker下载

```shell
docker pull elasticsearch:8.13.0
```

② 创建配置文件

```te
cluster.name: "nfturbo-cluster"
network.host: 0.0.0.0
http.cors.enabled: true
http.cors.allow-origin: "*"
xpack.security.enabled: false
```

③ 运行命令创建ES8容器

```shell
docker run --name elasticsearch -p 9200:9200  -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms256m -Xmx512m" -v C:/ZhangChuanSuper/nfturbo/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml -d elasticsearch:8.13.0
```

④ 安装完成之后访问地址 http://localhost:9200/ 进行验证，9200 端口为浏览器访问的 http 协议 RESTful 端口。



2、Doker安装kibana

① 查看es8的

```shell
docker inspect 248a763f171a(es8的容器ID)
```

内容如下：

```tex
[
    {
       "NetworkSettings": {
            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "DriverOpts": null,
                    "GwPriority": 0,
                    "NetworkID": "bd294253b2f1fcc43320917ac83b70ba98fc49ff67b14b249be793b46b876155",
                    "EndpointID": "2224fa4da63524b898f83fa4c412c592bafa7836d90c0385b09dfeb604451f4f",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "MacAddress": "46:67:3f:a6:15:64",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "DNSNames": null
                }
            }
        }
    }
]
```

将上面 IPAddress 的值记录下来



② 使用docker下载kibana

```shell
docker pull kibana:8.13.0
```

③ 创建kibana的配置文件kibana.yml

```tex
server.name: kibana
server.port: 5601
server.host: 0.0.0.0
elasticsearch.hosts: [ "http://172.17.0.2:9200" ]  
xpack.monitoring.ui.container.elasticsearch.enabled: true
i18n.locale: "zh-CN"
```

其中elasticsearch.hosts的值就是es的内网IP地址 IPAddress 

④ 启动Kibana

```shell
docker run --name kibana -d -p 5601:5601  -v C:/ZhangChuanSuper/nfturbo/config/kibana.yml:/usr/share/kibana/config/kibana.yml  kibana:8.13.0
```

⑤ 访问地址 http://localhost:5601/app/home#/ 进行验证是否安装成功



------

## 二、ES基础操作

1、索引的相关操作

① 创建索引(等同于创建数据库)

```sql
http://127.0.0.1:9200/shopping   // PUT请求
```

② 查看单个索引

```sql
http://127.0.0.1:9200/shopping   // GET请求
```

③ 查看所有索引

```sql
http://127.0.0.1:9200/_cat/indices?v
```

④ 删除索引

```sql
http://127.0.0.1:9200/shopping
```



2、文档相关操作（类似于表数据）

① 创建文档

```json
http://127.0.0.1:9200/shopping/_doc        // POST请求，请求体内容如下
http://127.0.0.1:9200/shopping/_doc/1001   // 指定ID
{
 "title":"小米手机",
 "category":"小米",
 "images":"http://www.gulixueyuan.com/xm.jpg",
 "price":3999.00
}
```



② 查看文档

```sql
http://127.0.0.1:9200/shopping/_doc/1001  // GET请求
```

③ 查看所有数据

```sql
http://127.0.0.1:9200/shopping/_search    // GET请求
```

返回值中的hits就是我们命中的结果。

④ ES全量更新

```tex
http://127.0.0.1:9200/shopping/_doc/1001  // PUT请求

// 请求体如下;
{
    "title": "华为手机",
    "category": "华为",
    "images": "http://www.gulixueyuan.com/xm.jpg",
    "price": 3999.00
}
```

⑤ 局部更新

```tex
http://127.0.0.1:9200/shopping/_update/1001  // POST请求

// 请求体如下
{
    "doc": {
        "title": "苹果手机"     
    }
}
```

⑥ 删除数据

```tex
http://127.0.0.1:9200/shopping/_doc/1001   // DELETE请求 
```



3、ES查询相关操作

① 条件查询

```tex
http://127.0.0.1:9200/shopping/_search?q=category:小米  // GET请求

```

也可以写为下面这种格式：

```tex
http://127.0.0.1:9200/shopping/_search   // GET请求

请求体中的内容如下所示：
{
    "query": {
        "match": {
            "category":"小米"
        }
    }
}
```

② 分页查询

```tex
http://127.0.0.1:9200/shopping/_search   // GET请求

请求体中的内容如下所示：
{
    "query": {
        "match": {
            "category":"小米"
        }
    },
    "from":0,
    "size":1
}
```



③ 指定查询字段

```tex
{
    "query": {
        "match": {
            "category":"小米"
        }
    },
    "from":0,
    "size":1, 
    "_source":  ["title"]    // 使用source定义查询哪些字段
}
```



④ 排序查询

```tex
{
    "query": {
        "match": {
            "category":"小米"
        }
    },
    "sort":{"price":{"order":"asc"}} 
}
```



## 三、ES基础API

























