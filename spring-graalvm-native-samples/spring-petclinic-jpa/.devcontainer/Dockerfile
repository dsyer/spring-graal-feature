FROM springci/spring-graalvm-native:20.1-dev-java11

ARG USER
ARG USER_ID
ARG USER_GID
ARG DOCKER_GID=130

RUN (groupadd --gid "${USER_GID}" "${USER}" || echo "No groupadd needed") && \
    useradd \
      --uid ${USER_ID} \
      --gid ${USER_GID} \
      --create-home \
      --shell /bin/bash \
      ${USER}

# download kustomize
RUN opsys=linux && curl -s https://api.github.com/repos/kubernetes-sigs/kustomize/releases |\
  grep browser_download |\
  grep $opsys |\
  cut -d '"' -f 4 |\
  grep /kustomize/v |\
  sort | tail -n 1 |\
  xargs curl -O -L \
  && tar xzf ./kustomize_v*_${opsys}_amd64.tar.gz && mv kustomize /usr/local/bin \
  && rm -rf ./kustomize_v*

# Install Docker CE CLI
RUN apt-get update \
    && apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common lsb-release \
    && curl -fsSL https://download.docker.com/linux/$(lsb_release -is | tr '[:upper:]' '[:lower:]')/gpg | apt-key add - 2>/dev/null \
    && add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/$(lsb_release -is | tr '[:upper:]' '[:lower:]') $(lsb_release -cs) stable" \
    && apt-get update \
    && apt-get install -y docker-ce-cli docker-compose \
    # Clean up
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid ${DOCKER_GID} docker \
    && usermod -aG docker ${USER}

RUN curl -sSL -o /usr/local/bin/kubectl https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl \
    && chmod +x /usr/local/bin/kubectl

RUN curl -Lo /usr/local/bin/skaffold https://storage.googleapis.com/skaffold/releases/latest/skaffold-linux-amd64 \
    && chmod +x /usr/local/bin/skaffold
