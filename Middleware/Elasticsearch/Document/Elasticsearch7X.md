## 一、DockerDesktop安装ES



1、Docker安装ES7

① 使用Docker下载

```shell
docker pull elasticsearch:7.8.0
```

② 创建配置文件

```tex
# 1. 集群与节点配置
cluster.name: my-es7-cluster      # 集群名称，同一集群内的节点必须相同
node.name: node-1                 # 当前节点名称，集群中需保持唯一

# 2. 路径配置（建议修改到自定义目录，避免权限问题）
path.data: /path/to/es/data       # 数据存放目录
path.logs: /path/to/es/logs       # 日志存放目录

# 3. 网络配置
network.host: 0.0.0.0             # 绑定所有IP，允许外部访问（单机测试也可用 localhost）
http.port: 9200                   # HTTP API 端口

# 4. 发现与选举配置（ES 7.x 核心变更）
discovery.type: single-node       # 单节点模式，避免集群自动发现报错
# 如果是多节点集群，请注释掉上面这行，并使用以下配置：
# discovery.seed_hosts: ["host1:9300", "host2:9300"]
cluster.initial_master_nodes: ["node-1"] # 初始化主节点列表，首次启动集群时必须配置

# 5. 跨域配置（解决 head 插件或前端直连报错）
http.cors.enabled: true           # 开启跨域访问支持
http.cors.allow-origin: "*"       # 允许所有来源访问
```

③ 运行命令创建ES7容器

```shell
docker run --name elasticsearch7 -p 9200:9200  -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms256m -Xmx512m" -v C:/ZhangChuanSuper/nfturbo/config/elasticsearch7.yml:/usr/share/elasticsearch/config/elasticsearch.yml -d elasticsearch:7.8.0
```

④ 安装完成之后访问地址 http://localhost:9200/ 进行验证，9200 端口为浏览器访问的 http 协议 RESTful 端口。



2、Docker安装对应kibana

① 使用docker下载kibana

```shell
docker pull kibana:8.13.0
```

② 创建kibana的配置文件kibana.yml

```tex
server.name: kibana
server.port: 5601
server.host: 0.0.0.0
elasticsearch.hosts: [ "http://172.17.0.2:9200" ]  
i18n.locale: "zh-CN"
```

其中elasticsearch.hosts的值就是es的内网IP地址 IPAddress 

④ 启动Kibana

```shell
docker run --name kibana7 -d -p 5601:5601  -v C:/ZhangChuanSuper/nfturbo/config/kibana7.yml:/usr/share/kibana/config/kibana.yml  kibana:7.8.0
```

⑤ 访问地址 http://localhost:5601/app/kibana#/home 进行验证是否安装成功



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



## 三、ES7基础API

























