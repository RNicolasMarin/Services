package com.example.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BasicService : Service() {

    override fun onCreate() {
        super.onCreate()
        //Se llama cuando el servicio se crea por primera vez.
        //Se utiliza para procedimientos de configuración únicos.
    }

    override fun onDestroy() {
        super.onDestroy()
        //Se llama cuando el servicio ya no está en uso y está
        // siendo destruido. Se utiliza para limpieza.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        //Para permitir que los componentes lo inicien.
        //Se llama cada vez que un cliente inicia el servicio usando startService().
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
        //Para permitir que los componentes se vinculen al servicio.
        //Se llama cuando un componente se vincula al servicio mediante bindService().
        //Devuelve una interfaz IBinder para comunicación.
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        //Se llama cuando todos los clientes se han desconectado de un servicio vinculado.
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        //Se llama cuando nuevos clientes se vuelven a vincular al servicio.
    }
}