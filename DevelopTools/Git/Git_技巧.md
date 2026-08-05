## GIT以及GITHUB相关使用

### 1、一个Git仓库同时推送到Gitee和GitHub

① 查看当前仓库的远程仓库配置

```shell
git remote -v
```

如果还没有添加任何远程仓库，可以先通过git remote add origin 添加

```shell
git remote add origin https://gitee.com/zhangchuanSuper/java-developer-collection-lts.git
```

②  为 origin 添加第二个推送地址

假设已经可以推送到GITEE了，还需要追加Github，执行下面命令

```shell
git remote set-url --add origin https://github.com/zhangchuanSuper/java-developer-collection-LTS.git
```

③ 验证配置是否成功

```shell
git remote -v
```

如果结果如下所示：

```tex
origin  https://gitee.com/zhangchuanSuper/java-developer-collection-lts.git (fetch)                                     
origin  https://gitee.com/zhangchuanSuper/java-developer-collection-lts.git (push)                                      
origin  https://github.com/zhangchuanSuper/java-developer-collection-LTS.git (push) 
```

上面可以看到fetch只有一条地址，但是push存在两条，说明配置成功。