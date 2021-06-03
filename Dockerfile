FROM markhobson/maven-chrome
USER root

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys D7CC6F019D06AF36
RUN apt-get update -y && apt-get install -y software-properties-common
RUN add-apt-repository ppa:cwchien/gradle && apt-get update -y && apt install -y gradle

USER jenkins