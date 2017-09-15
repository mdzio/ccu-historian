import mdz.hc.DataPoint

public class TrendGroup {
	int height
	List<DataPoint> dataPoints=[]
	
	@Override
	String toString() {
		"(height: $height, dataPoints: ${dataPoints.displayName})"
	}
}

