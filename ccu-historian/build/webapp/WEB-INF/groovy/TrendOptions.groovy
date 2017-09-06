import mdz.ccuhistorian.TrendDesign

public class TrendOptions {
	
	int width, height
	List<TrendGroup> groups=[]
	TrendDesign trendDesign
	
	@Override
	String toString() {
		"width: $width, height: $height, trendDesign: $trendDesign.identifier, groups: $groups"
	}
}