package models.Contents.charts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PieChart extends BasicChart{

    @Override
    public ChartTypes getType() {
        return ChartTypes.Pie;
    }
}
