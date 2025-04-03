
var fichamapId = "geovisor-map";
var fichaDform = 'geovisor-uidata';

function getPropData(name){
    return document.getElementById(name).value;
}

function sgm_loadMap(){
    sgmMap_geoserverUrl = getPropData(fichaDform+':geoserverUrl');
    sgmMap_srid = getPropData(fichaDform+':srid');
    sgmMap_backGroupLayer = getPropData(fichaDform+':backGroupLayer');
    /* WFS data: */
    sgmMap_predioWfsLayer = getPropData(fichaDform+':predioWfsLayer');
    var wfslayer_prefix = sgmMap_predioWfsLayer.split(":")[0];
    var wfslayer_type = sgmMap_predioWfsLayer.split(":")[1];
    sgmMap_predioWfsLayer_codigoField = getPropData(fichaDform+':predioWfsLayer_codigoField');
    sgmMap_predioWfsLayer_codigoPredio = getPropData(fichaDform+':predioWfsLayer_codigoPredio');
    sgmMap_predioWfsNsuri = getPropData(fichaDform+':predioWfsNsuri');
    
    sgmMap_bgLayer = new ol.layer.Tile({
          //extent: [-13884991, 2870341, -7455066, 6338219],
          source: new ol.source.TileWMS({
            url: sgmMap_geoserverUrl + '/wms',
            params: {'LAYERS': sgmMap_backGroupLayer, 'TILED': true},
            serverType: 'geoserver'
          })
    });
    
    sgmMap_predioVectorSource = new ol.source.Vector();
    sgmMap_predioVector = new ol.layer.Vector({
        source: sgmMap_predioVectorSource,
        style: new ol.style.Style({
          stroke: new ol.style.Stroke({
            color: 'rgba(0, 0, 255, 1.0)',
            width: 2
          })
        })
      });
    var raster = new ol.layer.Tile({
        source: new ol.source.BingMaps({
          imagerySet: 'Aerial',
          key: 'Your Bing Maps Key from http://www.bingmapsportal.com/ here'
        })
      });
    sgmMap_map = new ol.Map({
        layers: [sgmMap_bgLayer, sgmMap_predioVector],
        target: document.getElementById(fichamapId),
        view: new ol.View({
            projection: sgmMap_srid,
          center: [-8694848,45460],
          maxZoom: 21,
          zoom: 12
        })
      });
  
    var featureRequest = new ol.format.WFS().writeGetFeature({
        srsName: sgmMap_srid,
        featureNS: sgmMap_predioWfsNsuri,
        featurePrefix: wfslayer_prefix,
        featureTypes: [wfslayer_type],
        outputFormat: 'application/json',
        filter: ol.format.filter.equalTo(sgmMap_predioWfsLayer_codigoField, sgmMap_predioWfsLayer_codigoPredio)
//            ol.format.filter.and(
//              ol.format.filter.like('name', 'Mississippi*'),
//              ol.format.filter.equalTo('waterway', 'riverbank')
//            )
      });

    fetch(sgmMap_geoserverUrl + '/wfs', {
        method: 'POST',
        body: new XMLSerializer().serializeToString(featureRequest)
      }).then(function(response) {
        return response.json();
      }).then(function(json) {
        var features = new ol.format.GeoJSON().readFeatures(json);
        sgmMap_predioVectorSource.addFeatures(features);
        sgmMap_map.getView().fit(sgmMap_predioVectorSource.getExtent());
      });
  
}
//
//var vectorSource = new ol.source.Vector({
//    format: new ol.format.GeoJSON(),
//    url: function(extent) {
//      return 'https://ahocevar.com/geoserver/wfs?service=WFS&' +
//          'version=1.1.0&request=GetFeature&typename=osm:water_areas&' +
//          'outputFormat=application/json&srsname=EPSG:3857&' +
//          'bbox=' + extent.join(',') + ',EPSG:3857';
//    },
//    strategy: ol.loadingstrategy.bbox
//  });
//
//
//  var vector = new ol.layer.Vector({
//    source: vectorSource,
//    style: new ol.style.Style({
//      stroke: new ol.style.Stroke({
//        color: 'rgba(0, 0, 255, 1.0)',
//        width: 2
//      })
//    })
//  });

  

  





