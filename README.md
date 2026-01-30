# PTC Homework - Content tree editor

This application is a demo for editing a tree structure. Users can do multiple things like:
- add a child node to the selected node
- edit node's name and content
- delete the selected node and its children recursively
- relocate a node (with its children) to another node
- search by name and content

The **root node** cannot be deleted. The application persists the state in a `json` file.

# Build as a docker image

Clone this repository

```bash
git clone https://github.com/szodi/content-tree.git
```

Navigate to the `content-tree` folder and build docker image

```bash
cd content-tree
```

```bash
docker build --progress=plain --no-cache -t content-tree:latest .
```

It takes some time. When the image is ready, run it

```bash
docker run -p 8080:8080 content-tree:latest
```

# For developers

The API is generated with OpenAPI. If you want to build and run the application without Docker then **the first step has to be**

```bash
mvn clean install
```

It generates a `ptc-api.json` file under the `specs` folder in the **project root**
OpenAPI has a frontend task which runs when the dev server is started. `npm start` calls a `generate:api` task and it generates the client side DTO-s and services for Angular.
