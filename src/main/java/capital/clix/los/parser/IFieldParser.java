package capital.clix.los.parser;

import java.util.List;
import java.util.Map;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.los.entity.Field;

public interface IFieldParser {

  Map<String, String> parseForFields(List<Field> operands, LinkedTreeMap masterJson);
}
