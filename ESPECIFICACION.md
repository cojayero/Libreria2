# Especificación Técnica: Librería App

## Descripción General
Librería App es una aplicación Android para la gestión personal de libros físicos. Permite escanear, registrar, consultar y organizar libros, así como gestionar una lista de deseos. Utiliza Jetpack Compose (Material3), Hilt, Room, Retrofit y la API de Google Books.

---

## Estructura de Datos

### Book (Libro)
```kotlin
@Entity(tableName = "books")
data class Book(
    @PrimaryKey val isbn: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val price: Double?,
    val review: String?,
    val synopsis: String?,
    val bookcaseNumber: Int?,
    val shelfNumber: Int?,
    val editorial: String?,
    val pageCount: Int?,
    val addedDate: Long = System.currentTimeMillis()
)
```

### GoogleBooksApi (API Remota)
- **searchBookByIsbn(q: String): GoogleBooksResponse**
  - Endpoint: `GET /volumes?q=isbn:<ISBN>`
  - Respuesta:
    ```kotlin
    data class GoogleBooksResponse(val items: List<VolumeInfo>?)
    data class VolumeInfo(val volumeInfo: BookInfo, val saleInfo: SaleInfo?)
    data class BookInfo(
        val title: String,
        val authors: List<String>?,
        val description: String?,
        val imageLinks: ImageLinks?,
        val averageRating: Double?,
        val publisher: String?,
        val pageCount: Int?
    )
    data class SaleInfo(val listPrice: Price?)
    data class Price(val amount: Double)
    data class ImageLinks(val thumbnail: String?)
    ```

---

## Lógica de Negocio y Flujo

### 1. Registro y Consulta de Libros
- El usuario puede escanear el ISBN de un libro (MLKit + CameraX).
- Se consulta la API de Google Books para obtener los datos del libro.
- El usuario puede editar y guardar la ficha en la base local (Room).
- Se puede asignar ubicación física (estantería y repisa).
- El precio (en euros) se muestra si está disponible en la API.

### 2. Lista de Deseos (Wishlist)
- Permite añadir libros que el usuario desea adquirir.
- Se almacena en base local y puede consultarse desde la app.

### 3. Ubicación por Defecto
- El usuario puede fijar una ubicación por defecto (estantería y repisa).
- Al registrar un libro nuevo, si no se especifica ubicación, se muestra la ubicación por defecto en la ficha.
- La ubicación por defecto se almacena en `SharedPreferences` (vía `AppPreferences`).

### 4. Exportación
- Permite exportar la base de datos de libros a CSV y compartirla por email.

---

## Estructura de Carpetas
- `ui/` Pantallas y componentes de UI (Compose)
- `data/model/` Modelos de datos (Room, API)
- `data/local/` DAOs y base de datos Room
- `data/remote/` Servicios de red (Retrofit)
- `data/repository/` Lógica de acceso a datos
- `util/` Utilidades (preferencias, exportación, imágenes)

---

## Llamadas al API Externas
- **GET https://www.googleapis.com/books/v1/volumes?q=isbn:<ISBN>**
  - Devuelve información del libro, portada, sinopsis, editorial, número de páginas y precio si está disponible.

---

## Dependencias Principales
- Jetpack Compose (Material3)
- Hilt (Inyección de dependencias)
- Room (Base de datos local)
- Retrofit (Cliente HTTP)
- MLKit (Escaneo de códigos de barras)
- CameraX (Cámara)

---

## Notas de Uso
- El precio se muestra en euros (€) si está disponible.
- La ubicación por defecto se auto-rellena en la ficha si el libro no tiene una ubicación asignada.
- El usuario puede editar la ubicación desde la ficha o fijar una ubicación por defecto desde el menú principal.

---

## Autoría y Licencia
- Proyecto personal para gestión de biblioteca doméstica.
- Licencia: MIT (modificable según preferencia del autor).
