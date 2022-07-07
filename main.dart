import 'package:datapassing/second.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(
    home: PassingData(),
  ));
}

class PassingData extends StatefulWidget {
  const PassingData({Key? key}) : super(key: key);

  @override
  _PassingDataState createState() => _PassingDataState();
}

class _PassingDataState extends State<PassingData> {


  TextEditingController myController = TextEditingController();
  TextEditingController myController2 = TextEditingController();


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Data Passing"),),
      body: Padding(
        padding: const EdgeInsets.all(15.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
          TextFormField(

          controller: myController,
          keyboardType: TextInputType.number,
          decoration: InputDecoration(
              border: OutlineInputBorder(),
              labelText: "Mobile",
              hintText: "Enter Mobile"
          ),
        ),
            SizedBox(
              height: 20,
            ),

            TextFormField(
          controller: myController2,
          keyboardType: TextInputType.number,

          decoration: InputDecoration(
              border: OutlineInputBorder(),
              labelText: "Password",
              hintText: "Enter Password"
          ),
        ),
            SizedBox(
              height: 20,
            ),

            RaisedButton(
                child: Text("OnTap"),
                onPressed: () {
                  debugPrint("------------> ${myController.text}");
                  debugPrint("------------> ${myController2.text}");

               _sendotherScreen(context);

                }

            ),

          ],
        ),
      ),
    );
  }

  _sendotherScreen(BuildContext context){
    String userName = myController.text;
    String userPassword = myController2.text;

    Navigator.push(context,
        MaterialPageRoute(
            builder: (context)=> SecondScreen(userName: myController.text, userPassword:myController2.text ))
    );
  }


}

