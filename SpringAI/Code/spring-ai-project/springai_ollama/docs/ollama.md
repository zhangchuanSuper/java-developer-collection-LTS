

## ollama简介

Ollama是一个用于本地化部署和管理大型语言模型(LLM)的工具。它支持多种开源模型（如LLaMA、Alpaca等)，并提供了简单的API接口，方便开发者调用。Ollma可以让你在自已的电脑上运行各种强大的AI模型，就像运行普通软件一样简单。



## Windows本地安装ollama

- 官方（[ollama官网](https://ollama.com/)）下载ollama，然后进行安装

- ollama拉取模型

  ```shell
  ollama pull deepseek-r1:1.5b
  ```

- ollama运行模型

  ```shell
  ollama run deepseek-r1:1.5b
  ```

  在运行成功之后会默认监听 http://localhost:11434/ ，使用这个地址在浏览器中进行访问。