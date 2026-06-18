# Formula Fan - API Reference

Base URL: `http://localhost:8080`

---

## Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <token>
```

Tokens are issued by `/api/v1/auth/register` and `/api/v1/auth/login`. Validity: 24 hours.

Two roles exist:
- `CUSTOMER` - assigned to every new user
- `ADMIN` - seeded at startup via `application.yml` → `auth.admin.login`

---

## Error Responses

All errors return a JSON body:

```json
{
  "error": "Description of the error"
}
```

500 responses include an additional `"details"` field.

| HTTP Status | Cause |
|---|---|
| `400 Bad Request` | Invalid input (`IllegalArgumentException`) |
| `401 Unauthorized` | Invalid/expired JWT, wrong credentials, or non-ADMIN accessing moderation endpoints |
| `403 Forbidden` | Email or username already taken |
| `404 Not Found` | Resource not found (post, user, grand prix, moderation entry) |
| `500 Internal Server Error` | Unexpected error |

---

## Authentication (`/api/v1/auth`)

All endpoints in this group are **public** (no token required).

---

### `POST /api/v1/auth/register`

Create a new account. Returns a JWT token and the created user.

**Request body**

```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Response `200 OK`**

```json
{
  "token": "eyJhbGci...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "fanatic42",
    "role": "CUSTOMER"
  }
}
```

**Errors:** `403` if email or username already taken.

---

### `POST /api/v1/auth/login`

Authenticate with existing credentials.

**Request body**

```json
{
  "email": "string",
  "password": "string"
}
```

**Response `200 OK`** - same shape as `/register`.

**Errors:** `401` if credentials are incorrect.

---

### `POST /api/v1/auth/logout`

Client-side logout stub. Token is **not** invalidated server-side (stateless JWT).

**Response `200 OK`**

```
"Successfully logged out."
```

---

### `GET /api/v1/auth/validate`

Validate a JWT. Used internally by clients before making authenticated requests.

**Header:** `Authorization: Bearer <token>`

**Response `200 OK`** → `true`

**Response `401 Unauthorized`** (no body) - token missing, malformed, or expired.

---

## Posts (`/api/v1/posts`)

GET endpoints are **public**. The `Authorization` header is optional on GETs: when present, `isLiked` reflects the authenticated user's like state; when absent, `isLiked` is always `false`.

Write operations (`POST`, `PUT`, `DELETE`) require authentication.

---

### `GET /api/v1/posts`

Return all visible posts, sorted by creation date descending. Hidden posts are excluded.

**Header:** `Authorization: Bearer <token>` *(optional)*

**Response `200 OK`** - array of `PostResponseDTO` (see [Post response object](#post-response-object)).

---

### `POST /api/v1/posts`

Create a new post.

**Header:** `Authorization: Bearer <token>` *(required)*

**Request body**

```json
{
  "title": "string",
  "description": "string",
  "imageUrl": "string",
  "userId": 1,
  "grandPrixId": 3
}
```

**Response `200 OK`** - `PostResponseDTO`.

---

### `GET /api/v1/posts/{id}`

Return a single post by ID.

**Path parameter:** `id` (Long)

**Header:** `Authorization: Bearer <token>` *(optional)*

**Response `200 OK`** - `PostResponseDTO`.

**Errors:** `404` if post not found.

---

### `PUT /api/v1/posts/{id}`

Update an existing post.

**Path parameter:** `id` (Long)

**Header:** `Authorization: Bearer <token>` *(required)*

**Request body** - same shape as `POST /api/v1/posts`.

**Response `200 OK`** - updated `PostResponseDTO`.

**Errors:** `404` if post not found.

---

### `DELETE /api/v1/posts/{id}`

Delete a post.

**Path parameter:** `id` (Long)

**Header:** `Authorization: Bearer <token>` *(required)*

**Response `204 No Content`**

**Errors:** `404` if post not found.

---

### `GET /api/v1/posts/grand-prix/{grandPrixId}`

Return all visible posts for a Grand Prix, sorted by creation date descending.

**Path parameter:** `grandPrixId` (Long)

**Header:** `Authorization: Bearer <token>` *(optional)*

**Response `200 OK`** - array of `PostResponseDTO`.

---

### `GET /api/v1/posts/user/{userId}`

Return all visible posts by a user, sorted by creation date descending.

**Path parameter:** `userId` (Long)

**Header:** `Authorization: Bearer <token>` *(optional)*

**Response `200 OK`** - array of `PostResponseDTO`.

---

### `POST /api/v1/posts/{id}/like`

Like a post.

**Path parameter:** `id` (Long)

**Query parameter:** `user` (Long) - ID of the liking user

**Header:** `Authorization: Bearer <token>` *(required)*

**Response `200 OK`** (empty body)

**Errors:** `404` if post not found; `401` if the user has already liked the post.

---

### `POST /api/v1/posts/{id}/unlike`

Remove a like from a post.

**Path parameter:** `id` (Long)

**Query parameter:** `user` (Long) - ID of the user

**Header:** `Authorization: Bearer <token>` *(required)*

**Response `200 OK`** (empty body)

**Errors:** `404` if post not found.

---

### Post response object

```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "imageUrl": "string",
  "author": {
    "userId": 1,
    "username": "fanatic42"
  },
  "grandPrix": {
    "id": 3,
    "name": "Monaco"
  },
  "dateCreated": "2024-05-26T14:32:00",
  "likeCount": 12,
  "isLiked": false,
  "isHidden": false
}
```

---

## Grand Prix (`/api/v1/grand-prix`)

**Public** - no authentication required.

---

### `GET /api/v1/grand-prix`

Return all Grand Prix entries.

**Response `200 OK`**

```json
[
  { "id": 1, "name": "Bahrain" },
  { "id": 2, "name": "Saudi Arabia" }
]
```

---

## Moderation (`/api/v1/moderation`)

All endpoints require `ADMIN` role. Non-admin requests receive `401 Unauthorized`.

**Header on all requests:** `Authorization: Bearer <token>` *(required, ADMIN)*

---

### `POST /api/v1/moderation/hide`

Hide a post (flag it as moderated).

**Request body**

```json
{
  "postId": 5,
  "reason": "Inappropriate content"
}
```

**Response `200 OK`** (empty body)

**Errors:** `404` if post not found.

---

### `GET /api/v1/moderation/hidden-posts`

Return all currently hidden posts with moderation metadata.

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "post": { /* PostResponseDTO */ },
    "adminId": 2,
    "adminUsername": "admin",
    "reason": "Inappropriate content",
    "date": "2024-05-26T15:00:00"
  }
]
```

---

### `PUT /api/v1/moderation/{moderationId}/reason`

Update the moderation reason for a hidden post.

**Path parameter:** `moderationId` (Long)

**Request body**

```json
{
  "postId": 5,
  "reason": "Updated reason"
}
```

**Response `200 OK`** (empty body)

**Errors:** `404` if moderation entry not found.

---

### `DELETE /api/v1/moderation/{moderationId}/cancel`

Restore a hidden post (cancel moderation).

**Path parameter:** `moderationId` (Long)

**Response `204 No Content`**

**Errors:** `404` if moderation entry not found.

---

## Stats (`/api/v1/stats`)

**Public** - no authentication required.

---

### `GET /api/v1/stats/{userId}`

Return profile statistics for a user.

**Path parameter:** `userId` (Long)

**Response `200 OK`**

```json
{
  "totalPosts": 14,
  "totalLikesGiven": 37,
  "totalLikesReceived": 82
}
```

**Errors:** `404` if user not found.
