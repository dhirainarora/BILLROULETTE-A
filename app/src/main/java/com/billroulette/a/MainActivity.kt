package com.billroulette.a

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

data class Person(val name:String,val amount:Double)
enum class Screen { HOME, SETUP, ANIMATION, REVEAL, RESULTS, HISTORY, SETTINGS }
enum class Mode { FAIR, CHAOS, WILD, MAYHEM }

class MainActivity:ComponentActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContent{BillRouletteApp()}}
}

@Composable fun BillRouletteApp(){
 var screen by remember{mutableStateOf(Screen.HOME)}
 var people by remember{mutableIntStateOf(4)}
 var names by remember{mutableStateOf(List(4){""})}
 var bill by remember{mutableStateOf("")}
 var mode by remember{mutableStateOf(Mode.CHAOS)}
 var currency by remember{mutableStateOf("INR")}
 var sound by remember{mutableStateOf(true)}
 var haptics by remember{mutableStateOf(true)}
 var result by remember{mutableStateOf<List<Person>>(emptyList())}
 var history by remember{mutableStateOf<List<List<Person>>>(emptyList())}
 val context=LocalContext.current
 MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF7C3AED),background=Color(0xFF09090D),surface=Color(0xFF111118))){
  Surface(Modifier.fillMaxSize(),color=Color(0xFF09090D)){
   when(screen){
    Screen.HOME->Home({screen=Screen.SETUP},{screen=Screen.HISTORY},{screen=Screen.SETTINGS},history.size,sound,{sound=!sound})
    Screen.SETUP->Setup(people,{v->people=v;names=List(v){i->names.getOrNull(i)?:""}},names,{i,v->names=names.toMutableList().also{it[i]=v}},bill,{bill=it},mode,{mode=it},currency,{currency=it},{
     val total=bill.toDoubleOrNull()?:0.0
     if(total>0&&people>=2){result=allocate(total,names,mode);history=listOf(result)+history.take(49);screen=Screen.ANIMATION;vibrate(context,haptics)}
    }){screen=Screen.HOME}
    Screen.ANIMATION->Animation({screen=Screen.REVEAL})
    Screen.REVEAL->Reveal(result,currency){screen=Screen.RESULTS}
    Screen.RESULTS->Results(result,currency,{screen=Screen.SETUP}){shareResult(context,result,currency)}
    Screen.HISTORY->HistoryScreen(history,currency){screen=Screen.HOME}
    Screen.SETTINGS->Settings(sound,{sound=!sound},haptics,{haptics=!haptics},currency,{currency=it},history.size,{history=emptyList()}){screen=Screen.HOME}
   }
  }
 }
}

@Composable fun Header(title:String,back:(()->Unit)?=null){
 Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){
  back?.let{IconButton(it){Icon(Icons.Default.ArrowBack,"Back")}}
  Text(title,fontSize=20.sp,fontWeight=FontWeight.Bold)
 }
}

@Composable fun Home(start:()->Unit,history:()->Unit,settings:()->Unit,count:Int,sound:Boolean,toggle:()->Unit){
 Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.SpaceBetween){
  Column(horizontalAlignment=Alignment.CenterHorizontally){
   Text("⚡",fontSize=56.sp);Text("Split Roulette",fontSize=42.sp,fontWeight=FontWeight.ExtraBold)
   Text("Don't split the bill.",color=Color.LightGray,fontSize=18.sp);Text("Let fate split it.",color=Color(0xFFB197FC),fontWeight=FontWeight.Bold,fontSize=18.sp)
   Spacer(Modifier.height(24.dp))
   Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF111118))){Column(Modifier.padding(18.dp)){Text("Controlled Randomness",fontWeight=FontWeight.Bold);Text("Choose Fair, Chaos, Wild, or Mayhem.",color=Color.Gray);Spacer(Modifier.height(12.dp));Text("Guaranteed Exact Total",fontWeight=FontWeight.Bold);Text("Shares always add up to the bill.",color=Color.Gray)}}
  }
  Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){
   Button(start,Modifier.fillMaxWidth().height(56.dp)){Text("LET FATE DECIDE",fontWeight=FontWeight.Bold)}
   Spacer(Modifier.height(10.dp));OutlinedButton(history,Modifier.fillMaxWidth().height(52.dp)){Icon(Icons.Default.History,"History");Spacer(Modifier.width(8.dp));Text(if(count>0)"View History ("+count+")" else "History")}
   Row{IconButton(toggle){if(sound)Icon(Icons.Default.VolumeUp,"Sound")else Icon(Icons.Default.VolumeOff,"Sound")};IconButton(settings){Icon(Icons.Default.Settings,"Settings")}}
  }
 }
}

@Composable fun Setup(people:Int,setPeople:(Int)->Unit,names:List<String>,setName:(Int,String)->Unit,bill:String,setBill:(String)->Unit,mode:Mode,setMode:(Mode)->Unit,currency:String,setCurrency:(String)->Unit,confirm:()->Unit,cancel:()->Unit){
 LazyColumn(Modifier.fillMaxSize().padding(horizontal=20.dp)){
  item{Header("Setup",cancel);Text("People",fontWeight=FontWeight.Bold);Row(verticalAlignment=Alignment.CenterVertically){Button({if(people<20)setPeople(people+1)}){Text("+")};Text(people.toString(),Modifier.padding(horizontal=20.dp),fontSize=22.sp);Button({if(people>2)setPeople(people-1)}){Text("-")}}
   Spacer(Modifier.height(12.dp));OutlinedTextField(bill,setBill,Modifier.fillMaxWidth(),label={Text("Bill amount")});Spacer(Modifier.height(16.dp));Text("Currency",fontWeight=FontWeight.Bold)
   Row{listOf("INR","USD","EUR","GBP").forEach{c->FilterChip(c==currency,{setCurrency(c)},{Text(c)});Spacer(Modifier.width(4.dp))}}
   Spacer(Modifier.height(16.dp));Text("Mode",fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){Mode.values().forEach{m->FilterChip(m==mode,{setMode(m)},{Text(m.name)})}}
   Spacer(Modifier.height(16.dp));Text("Names",fontWeight=FontWeight.Bold)}
  itemsIndexed(names){i,v->OutlinedTextField(v,{setName(i,it)},Modifier.fillMaxWidth().padding(vertical=4.dp),label={Text("Person "+(i+1))})}
  item{Spacer(Modifier.height(16.dp));Button(confirm,Modifier.fillMaxWidth().height(56.dp)){Text("LET FATE DECIDE",fontWeight=FontWeight.Bold)};TextButton(cancel,Modifier.fillMaxWidth()){Text("Cancel")};Spacer(Modifier.height(30.dp))}
 }
}

fun allocate(total:Double,names:List<String>,mode:Mode):List<Person>{
 val n=names.size
 val w=DoubleArray(n){i->when(mode){Mode.FAIR->0.5+Random.nextDouble()*1.5;Mode.CHAOS->0.2+Random.nextDouble()*8.0;Mode.WILD->if(i==0)10.0+Random.nextDouble()*8.0 else 0.2+Random.nextDouble();Mode.MAYHEM->if(i<2)30.0+Random.nextDouble()*20.0 else 0.03+Random.nextDouble()*0.08}}
 val target=(total*100).roundToInt();val sum=w.sum();val cents=w.map{(it/sum*target).toInt()}.toMutableList();var rem=target-cents.sum();var cursor=0
 while(rem>0){cents[cursor%n]++;cursor++;rem--}
 return cents.mapIndexed{i,c->Person(names[i].ifBlank{"Person "+(i+1)},c/100.0)}.sortedByDescending{it.amount}
}

@Composable fun Animation(done:()->Unit){
 LaunchedEffect(Unit){delay(3000);done()}
 Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
  Text("FATE IS DECIDING",color=Color(0xFFA78BFA));Text("Consulting Fate...",fontSize=30.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(30.dp));CircularProgressIndicator();Spacer(Modifier.height(30.dp));TextButton(done){Text("Skip animation")}
 }
}

@Composable fun Reveal(result:List<Person>,currency:String,done:()->Unit){
 val p=result.firstOrNull()
 Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
  Text("FATE HAS SPOKEN",fontSize=30.sp,fontWeight=FontWeight.ExtraBold);Spacer(Modifier.height(18.dp));Text(p?.name?:"",fontSize=28.sp,fontWeight=FontWeight.Bold);Text("pays",color=Color.Gray)
  Text(p?.let{formatCurrency(it.amount,currency)}?:"",fontSize=46.sp,color=Color(0xFFA78BFA),fontWeight=FontWeight.ExtraBold);Spacer(Modifier.height(28.dp));Button(done){Text("SHOW RESULTS")}
 }
}

@Composable fun Results(result:List<Person>,currency:String,newSplit:()->Unit,share:()->Unit){
 Column(Modifier.fillMaxSize().padding(20.dp)){Header("Fate Has Spoken")
  LazyColumn(Modifier.weight(1f)){itemsIndexed(result){i,p->Card(Modifier.fillMaxWidth().padding(vertical=5.dp),colors=CardDefaults.cardColors(containerColor=if(i==0)Color(0x332167F3)else Color(0xFF111118))){Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){Text((i+1).toString()+". "+p.name,fontWeight=FontWeight.SemiBold);Text(formatCurrency(p.amount,currency),fontWeight=FontWeight.Bold,color=Color(0xFFA78BFA))}}}}
  Button(newSplit,Modifier.fillMaxWidth()){Text("NEW SPLIT")};OutlinedButton(share,Modifier.fillMaxWidth()){Icon(Icons.Default.Share,"Share");Spacer(Modifier.width(8.dp));Text("Share Results")}
 }
}

@Composable fun HistoryScreen(history:List<List<Person>>,currency:String,back:()->Unit){
 Column(Modifier.fillMaxSize()){Header("History",back)
  if(history.isEmpty())Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text("No saved splits",color=Color.Gray)}
  else LazyColumn(Modifier.padding(16.dp)){itemsIndexed(history){i,split->Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(14.dp)){Text("Split #"+(history.size-i),fontWeight=FontWeight.Bold);Text(split.map{p->p.name+": "+formatCurrency(p.amount,currency)}.joinToString(),color=Color.Gray)}}}}
 }
}

@Composable fun Settings(sound:Boolean,toggleSound:()->Unit,haptics:Boolean,toggleHaptics:()->Unit,currency:String,setCurrency:(String)->Unit,count:Int,clear:()->Unit,back:()->Unit){
 Column(Modifier.fillMaxSize().padding(20.dp)){Header("Settings",back);Text("Default Currency",fontWeight=FontWeight.Bold)
  Row{listOf("INR","USD","EUR","GBP").forEach{c->FilterChip(c==currency,{setCurrency(c)},{Text(c)});Spacer(Modifier.width(4.dp))}}
  Spacer(Modifier.height(16.dp));Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween){Text("Sound Effects");Switch(sound,{toggleSound()})};Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween){Text("Haptic Feedback");Switch(haptics,{toggleHaptics()})}
  Spacer(Modifier.height(16.dp));Text("Saved splits: "+count);TextButton(clear){Text("Clear History")}
 }
}

fun formatCurrency(v:Double,c:String)=when(c){"USD"->"$%.2f".format(v);"EUR"->"€%.2f".format(v);"GBP"->"£%.2f".format(v);else->"₹%.2f".format(v)}
fun vibrate(ctx:Context,on:Boolean){if(!on)return;val v=ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;if(android.os.Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(20,VibrationEffect.DEFAULT_AMPLITUDE))else @Suppress("DEPRECATION") v.vibrate(20)}
fun shareResult(ctx:Context,result:List<Person>,currency:String){val text=buildString{append("⚡ Split Roulette — Fate Has Spoken\n");result.forEachIndexed{i,p->append((i+1).toString()+". "+p.name+": "+formatCurrency(p.amount,currency)+"\n")}};val send=Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,text)};ctx.startActivity(Intent.createChooser(send,"Share results"))}
