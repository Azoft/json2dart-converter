import 'dart:convert';
import 'dart:io';

import 'package:dart/response.dart';

//Future<void> main(List<String> arguments) async {
main(List<String> arguments)  {
    final response = Response.fromJsonMap(
      jsonDecode(
          File(arguments[0]).readAsStringSync()
      )
    );
    File(arguments[1]).writeAsStringSync(jsonEncode(response));
}
