@file:JvmName("GetDirection")
package com.example.susa.mapUtils

import android.graphics.Color
import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception


class GetDirection(val url : String, val mMap:GoogleMap): AsyncTask<Void, Void, List<List<LatLng>>>(){
    override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val data = response.body?.string()
        val result = ArrayList<List<LatLng>>()
        try {
            val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
            val path = ArrayList<LatLng>()
            for(i in 0 until respObj.routes[0].legs[0].steps.size){
                val startLat = respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
                val startLng = respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble()
                val endLat = respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
                val endLng = respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble()

                val startLatLng = LatLng(startLat, startLng)
                path.add(startLatLng)
                path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
            }
            result.add(path)
        }catch (e : Exception){
            e.printStackTrace()
        }
        return result

    }

    override fun onPostExecute(result: List<List<LatLng>>) {
        val lineOption = PolylineOptions()
        for(i in result.indices){
            lineOption.addAll(result[i])
            lineOption.width(10f)
            lineOption.color(Color.BLUE)
            lineOption.geodesic(true)
        }
        mMap.addPolyline(lineOption)
    }

    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
}