package org.neidhardt.osmwrapper

import android.os.Bundle
import android.os.Environment
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File

/**
 * Created by neid_ei (eric.neidhardt@dlr.de)
 * on 21.12.2017.
 */
abstract class MapsforgeActivity : LocationAwareActivity() {

	abstract var mapView: MapView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		AndroidGraphicFactory.createInstance(this.application)
	}

	protected fun initMapView(mapFileName: String) {
		this.mapView.isClickable = true
		this.mapView.mapScaleBar.isVisible = true
		this.mapView.setBuiltInZoomControls(true)
		this.mapView.setZoomLevelMin(10.toByte())
		this.mapView.setZoomLevelMax(21.toByte())

		// create a tile cache of suitable size
		val tileCache = AndroidUtil.createTileCache(this, "mapcache",
				mapView.model.displayModel.tileSize, 1f,
				this.mapView.model.frameBufferModel.overdrawFactor)

		// tile renderer layer using internal render theme
		val mapDataStore = MapFile(File(Environment.getExternalStorageDirectory(), mapFileName))
		val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
				this.mapView.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)

		// only once a layer is associated with a mapView the rendering starts
		this.mapView.layerManager.layers.add(tileRendererLayer)
	}
}