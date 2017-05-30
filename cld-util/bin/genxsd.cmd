set project_root=../

xjc -d %project_root%/src/main/java -p org.cldutil.xml.taskdef -b %project_root%/src/main/resources/taskbinding_2_0.xml %project_root%/src/main/resources/Task_2_0.xsd
