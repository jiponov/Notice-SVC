# Notice-SVC ⚙️
📄 Notice SVC is a microservice responsible for handling notifications and generating license certificates for purchased games. It communicates with the main **Jubbisoft Games Store** application through REST APIs.
`(The project is still being developed and some features may be incomplete or unstable.)`

⚠️ **Project Integration**
This microservice is part of Jubbisoft Games Store.

## All Projects 🔗
- `[Jubbisoft Games Store (Main Application) link]`  https://github.com/jiponov/Jubbisoft-Games-Store
- `[Notice SVC (Microservice) link]`  https://github.com/jiponov/Notice-SVC

## API Endpoints 🔗
- `GET /notices/download/{gameId}/{userId}` → Download license file.
- `POST /notices` → Create new notice.

## Features 🚀
- **REST API Integration:** Provides GET and POST endpoints.
- **Independent Database:** Stores purchase records separately from Jubbisoft.
- **License Certificate Generation:** Creates a downloadable `.txt` license file.

## Disclaimer 📜
> **DISCLAIMER:** This project is created for educational purposes only. No real purchases, licenses, transactions or legal obligations exist.