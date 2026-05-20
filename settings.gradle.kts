rootProject.name = "travel-mono-repo"

// 1. Core Service
include("travel-core:domain")
include("travel-core:business")
include("travel-core:infrastructure")

// 2. Backend for Frontend (BFF)
include("travel-bff:domain")
include("travel-bff:business")
include("travel-bff:infrastructure")

// 3. Frontend (Angular SPA)
include("travel-ui")