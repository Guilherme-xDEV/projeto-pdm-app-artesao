package com.example.nprojetoartesanato.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseClient {

    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    val artesaosCollection: CollectionReference
        get() = db.collection("artesaos")

    val produtosCollection: CollectionReference
        get() = db.collection("produtos")

    val vendasCollection: CollectionReference
        get() = db.collection("vendas")
}
