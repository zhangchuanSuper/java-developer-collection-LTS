1、现有代码管理问题描述

当前系统进行公用服务拆分，但是每一个项目使用的公用服务代码和项目业务代码在同一个GIT仓库中，当不同项

目都对公用服务代码进行更改的话，此时不方便进行最新代码的迁移，也不方便管理。



2、为什么使用GIT子模块

当多个项目需要使用公共服务时，可以将公共服务代码放在单独的GIT仓库中，并通过子模块的方式引入到各个项

目中。这样，当公共服务代码更新时，只需在子模块GIT仓库中进行更新，然后在各个项目中更新子模块引用即

可，无需在每个项目中手动复制粘贴更新。



3、子模块的用法

创建主项目，如果要引入其他仓库的代码作为子模块，可以使用命令

git submodule add submoduleRepoAddress path

submoduleRepoAddress：子模块GIT仓库地址

path：子项目在主项目的目录名称，如果没有指定则使用子模块仓库中的默认名称。



拉取子模块的更新，使用命令 git submodule update --remote ，此时GIT 将会进入子模块然后抓取并更新。

GIT 默认会尝试更新 **所有** 子模块， 所以如果有很多子模块的话，你可以传递想要更新的子模块的名字。





4、如何拉取项目到本地（克隆含有子模块的项目）

1、git clone 主项目地址

2、进入本地主项目地址下，执行命令  git submodule init   和 git submodule update

在git clone命令执行时，主项目所含有代码已经拉取，但是子模块此时只有目录存在，但是没有任何文件。此时

需要执行命令：`git submodule init` 用来初始化本地配置文件，而 `git submodule update` 则从该项目中抓取所有数据并检出父项目中列出的合适的提交。

以上比较复杂，存在更简单的方式：

// 自动初始化并更新仓库中的每一个子模块， 包括可能存在的嵌套子模块。

git clone --recurse-submodules 项目GIT地址

已经克隆了项目但忘记了 `--recurse-submodules`，那么可以运行 `git submodule update --init` 将 `git submodule init` 和 `git submodule update` 合并成一步。如果还要初始化、抓取并检出任何嵌套的子模块， 请使用简明的 `git submodule update --init --recursive`。



5、使用SourceTree上传代码遇到的问题

A、如果使用sourceTree不能正常推送到远程仓库中，可能是因为SourceTree未进行ssh相关配置。配置如下：工具 -> 选项 -> SSH客户端配置

![img](https://cdn.nlark.com/yuque/0/2024/png/21994751/1735553633882-0e999e36-37fd-4ce1-b7b7-4ebacfed877d.png) 

B、使用SourceTree进行代码提交的时候，必须在拉下远程分支到本地后，切换自己的分支到指定的分支上，如果没有切换到本地指定分支，此后提交的代码会提交到GIT的游离分支上，此时推送不到远程对应的分支上去。

C、在提交代码的时候，如果修改了子模块，则先提交子模块的代码，然后再提交业务主模块的代码，此时必须提交子模块的版本号









------

引用：

1、GIT子模块官方介绍 ：

[https://git-scm.com/book/zh/v2/Git-%E5%B7%A5%E5%85%B7-%E5%AD%90%E6%A8%A1%E5%9D%97](https://git-scm.com/book/zh/v2/Git-工具-子模块)



2、GIT的游离分支：

https://blog.csdn.net/2401_87873725/article/details/143811612

https://blog.csdn.net/weixin_44961829/article/details/135951549