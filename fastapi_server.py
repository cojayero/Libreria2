from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, List
from fastapi.middleware.cors import CORSMiddleware
import sqlite3

app = FastAPI()

# Permitir CORS para pruebas en red local
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Modelos
class Book(BaseModel):
    isbn: str
    title: str
    author: str
    coverUrl: Optional[str]
    price: Optional[float]
    review: Optional[str]
    synopsis: Optional[str]
    bookcaseNumber: Optional[int]
    shelfNumber: Optional[int]
    editorial: Optional[str]
    pageCount: Optional[int]
    addedDate: Optional[int]

class WishlistBook(BaseModel):
    isbn: str
    title: str
    author: str
    coverUrl: Optional[str]
    price: Optional[float]
    editorial: Optional[str]
    pageCount: Optional[int]
    addedDate: Optional[int]

# --- DB helpers ---
def get_db():
    conn = sqlite3.connect("libreria.db")
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    conn = get_db()
    c = conn.cursor()
    c.execute('''CREATE TABLE IF NOT EXISTS books (
        isbn TEXT PRIMARY KEY,
        title TEXT,
        author TEXT,
        coverUrl TEXT,
        price REAL,
        review TEXT,
        synopsis TEXT,
        bookcaseNumber INTEGER,
        shelfNumber INTEGER,
        editorial TEXT,
        pageCount INTEGER,
        addedDate INTEGER
    )''')
    c.execute('''CREATE TABLE IF NOT EXISTS wishlist (
        isbn TEXT PRIMARY KEY,
        title TEXT,
        author TEXT,
        coverUrl TEXT,
        price REAL,
        editorial TEXT,
        pageCount INTEGER,
        addedDate INTEGER
    )''')
    conn.commit()
    conn.close()

init_db()

# --- Endpoints Book ---
@app.get("/books", response_model=List[Book])
def get_books():
    conn = get_db()
    books = conn.execute("SELECT * FROM books").fetchall()
    conn.close()
    return [Book(**dict(row)) for row in books]

@app.post("/books", response_model=Book)
def add_book(book: Book):
    conn = get_db()
    try:
        conn.execute("""
            INSERT INTO books (isbn, title, author, coverUrl, price, review, synopsis, bookcaseNumber, shelfNumber, editorial, pageCount, addedDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (book.isbn, book.title, book.author, book.coverUrl, book.price, book.review, book.synopsis, book.bookcaseNumber, book.shelfNumber, book.editorial, book.pageCount, book.addedDate))
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        raise HTTPException(status_code=409, detail="Book already exists")
    conn.close()
    return book

@app.put("/books/{isbn}", response_model=Book)
def update_book(isbn: str, book: Book):
    conn = get_db()
    c = conn.cursor()
    c.execute("""
        UPDATE books SET title=?, author=?, coverUrl=?, price=?, review=?, synopsis=?, bookcaseNumber=?, shelfNumber=?, editorial=?, pageCount=?, addedDate=? WHERE isbn=?
    """, (book.title, book.author, book.coverUrl, book.price, book.review, book.synopsis, book.bookcaseNumber, book.shelfNumber, book.editorial, book.pageCount, book.addedDate, isbn))
    if c.rowcount == 0:
        conn.close()
        raise HTTPException(status_code=404, detail="Book not found")
    conn.commit()
    conn.close()
    return book

@app.delete("/books/{isbn}")
def delete_book(isbn: str):
    conn = get_db()
    c = conn.cursor()
    c.execute("DELETE FROM books WHERE isbn=?", (isbn,))
    if c.rowcount == 0:
        conn.close()
        raise HTTPException(status_code=404, detail="Book not found")
    conn.commit()
    conn.close()
    return {"ok": True}

# --- Endpoints Wishlist ---
@app.get("/wishlist", response_model=List[WishlistBook])
def get_wishlist():
    conn = get_db()
    books = conn.execute("SELECT * FROM wishlist").fetchall()
    conn.close()
    return [WishlistBook(**dict(row)) for row in books]

@app.post("/wishlist", response_model=WishlistBook)
def add_wishlist(book: WishlistBook):
    conn = get_db()
    try:
        conn.execute("""
            INSERT INTO wishlist (isbn, title, author, coverUrl, price, editorial, pageCount, addedDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (book.isbn, book.title, book.author, book.coverUrl, book.price, book.editorial, book.pageCount, book.addedDate))
        conn.commit()
    except sqlite3.IntegrityError:
        conn.close()
        raise HTTPException(status_code=409, detail="Wishlist book already exists")
    conn.close()
    return book

@app.put("/wishlist/{isbn}", response_model=WishlistBook)
def update_wishlist(isbn: str, book: WishlistBook):
    conn = get_db()
    c = conn.cursor()
    c.execute("""
        UPDATE wishlist SET title=?, author=?, coverUrl=?, price=?, editorial=?, pageCount=?, addedDate=? WHERE isbn=?
    """, (book.title, book.author, book.coverUrl, book.price, book.editorial, book.pageCount, book.addedDate, isbn))
    if c.rowcount == 0:
        conn.close()
        raise HTTPException(status_code=404, detail="Wishlist book not found")
    conn.commit()
    conn.close()
    return book

@app.delete("/wishlist/{isbn}")
def delete_wishlist(isbn: str):
    conn = get_db()
    c = conn.cursor()
    c.execute("DELETE FROM wishlist WHERE isbn=?", (isbn,))
    if c.rowcount == 0:
        conn.close()
        raise HTTPException(status_code=404, detail="Wishlist book not found")
    conn.commit()
    conn.close()
    return {"ok": True}

# --- Endpoint de salud ---
@app.get("/")
def root():
    return {"status": "ok"}
