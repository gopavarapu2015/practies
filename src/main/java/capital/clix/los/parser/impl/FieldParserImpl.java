package capital.clix.los.parser.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.los.entity.Field;
import capital.clix.los.enums.CollectionAction;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.exception.LosException;
import capital.clix.los.parser.IFieldParser;
import capital.clix.util.JsonMarshallingUtil;

@Service("fieldParserImpl")
public class FieldParserImpl implements IFieldParser {

  @Override
  public Map<String, String> parseForFields(List<Field> fields, LinkedTreeMap masterJson) {
    Map<String, String> processedData = new HashMap<>();

    fields.forEach(field -> processedData.put(field.getName(),
        JsonMarshallingUtil.toString(decoupleAndFetch(field.getPath(), masterJson))));
    return processedData;
  }

  private Object decoupleAndFetch(String field, LinkedTreeMap masterData) {
    List<String> fieldPath = Arrays.asList(field.split("\\."));
    return getValue(-1, fieldPath, field, masterData);
  }

  private Object getValue(int parent, List<String> fieldPath, String field,
      LinkedTreeMap masterData) {
    int current = parent + 1;
    String currentPart = fieldPath.get(current);

    if (currentPart.contains("<")) {
      String operation = StringUtils.substringBetween(currentPart, "<", ">");
      return getCollectionValue(current, operation, fieldPath, field,
          (List) masterData.get(currentPart.replace("<" + operation + ">", "")));
    }

    if (!masterData.containsKey(currentPart)) {
      return null;
    }

    if (current == (fieldPath.size() - 1)) {
      return masterData.get(currentPart);
    }

    return getValue(current, fieldPath, field, (LinkedTreeMap) masterData.get(currentPart));
  }

  private Object getCollectionValue(int current, String operation, List<String> fieldPath,
      String field, List masterData) {
    if (CollectionUtils.isEmpty(masterData)) {
      return null;
    }
    CollectionAction action = CollectionAction.fromString(operation);
    switch (action) {
      case GET:
        return getValue(current, fieldPath, field,
            (LinkedTreeMap) masterData.get(Integer.parseInt(operation)));
      case ALL:
        List<Object> valueList = new ArrayList<>(0);
        // current += 1;
        for (Object jsonNode : masterData) {
          LinkedTreeMap node = (LinkedTreeMap) jsonNode;
          Object value = getValue(current, fieldPath, field, node);
          if (ObjectUtils.isEmpty(value)) {
            return null;
          }
          valueList.add(value);
        }
        return valueList;

      default:
        throw new LosException(ErrorState.INVALID_COLLECTION_ACTION,
            "operation " + operation + " invalid for field " + field);
    }
  }

  // private Object performSpecial(Object value, String operand) {
  // switch (operand.getSpecial()) {
  // case TOTAL:
  // List<Double> data = (List<Double>) value;
  // return data.stream().reduce((d1, d2) -> d1 + d2).get();
  // case AVERAGE:
  // data = (List<Double>) value;
  // return data.stream().mapToDouble(Double::doubleValue).sum() / data.size();
  // case NA:
  // default:
  // return value;
  // }
  // }

}
