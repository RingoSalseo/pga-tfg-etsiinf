package cartociudad;

public class ObjetoCartoCiudad {
	private String id;
    private String province;
    private String provinceCode;
    private String comunidadAutonoma;
    private String comunidadAutonomaCode;
    private String muni;
    private String muniCode;
    private String type;
    private String address;
    private String postalCode;
    private String poblacion;
    private String geom;
    private String tip_via;
    private double lat;
    private double lng;
    private int portalNumber;
    private boolean noNumber;
    private String stateMsg;
    private String extension;
    private int state;
    private String refCatastral;
    private String countryCode;

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getComunidadAutonoma() {
        return comunidadAutonoma;
    }

    public void setComunidadAutonoma(String comunidadAutonoma) {
        this.comunidadAutonoma = comunidadAutonoma;
    }

    public String getComunidadAutonomaCode() {
        return comunidadAutonomaCode;
    }

    public void setComunidadAutonomaCode(String comunidadAutonomaCode) {
        this.comunidadAutonomaCode = comunidadAutonomaCode;
    }

    public String getMuni() {
        return muni;
    }

    public void setMuni(String muni) {
        this.muni = muni;
    }

    public String getMuniCode() {
        return muniCode;
    }

    public void setMuniCode(String muniCode) {
        this.muniCode = muniCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public String getTip_via() {
        return tip_via;
    }

    public void setTip_via(String tip_via) {
        this.tip_via = tip_via;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getPortalNumber() {
        return portalNumber;
    }

    public void setPortalNumber(int portalNumber) {
        this.portalNumber = portalNumber;
    }

    public boolean isNoNumber() {
        return noNumber;
    }

    public void setNoNumber(boolean noNumber) {
        this.noNumber = noNumber;
    }

    public String getStateMsg() {
        return stateMsg;
    }

    public void setStateMsg(String stateMsg) {
        this.stateMsg = stateMsg;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRefCatastral() {
        return refCatastral;
    }

    public void setRefCatastral(String refCatastral) {
        this.refCatastral = refCatastral;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

	@Override
	public String toString() {
		return "ObjetoCartoCiudad [id=" + id + ", province=" + province + ", provinceCode=" + provinceCode
				+ ", comunidadAutonoma=" + comunidadAutonoma + ", comunidadAutonomaCode=" + comunidadAutonomaCode
				+ ", muni=" + muni + ", muniCode=" + muniCode + ", type=" + type + ", address=" + address
				+ ", postalCode=" + postalCode + ", poblacion=" + poblacion + ", geom=" + geom + ", tip_via=" + tip_via
				+ ", lat=" + lat + ", lng=" + lng + ", portalNumber=" + portalNumber + ", noNumber=" + noNumber
				+ ", stateMsg=" + stateMsg + ", extension=" + extension + ", state=" + state + ", refCatastral="
				+ refCatastral + ", countryCode=" + countryCode + "]";
	}
}
