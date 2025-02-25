#!/bin/bash

# 创建必要的目录
mkdir -p demo/docs
mkdir -p demo/src

# 确保所有文件都存在
touch docs/prd.md
touch docs/system_design.md
touch docs/dove-accelerator_class_diagram.mermaid
touch docs/dove-accelerator_sequence_diagram.mermaid
touch demo/docs/todo_app_prd.md

# 打包所有文件
tar -czf workspace.tar.gz \
    docs/ \
    demo/ \
    download.sh

echo "工作空间已打包为 workspace.tar.gz"
echo "请使用以下命令下载文件："
echo "scp user@server:/data/chats/pzyz5/workspace/workspace.tar.gz ./"
echo "然后解压：tar -xzf workspace.tar.gz"