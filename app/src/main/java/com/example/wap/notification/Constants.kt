package com.example.wap.notification

class Constants {

    companion object{
        const val BASE_URL = "https://fcm.googleapis.com"
        const val CONTENT_TYPE = "application/json"
        const val SERVER_KEY = "AAAAgLCSOd4:APA91bGU6FqUY6t_29jW9bKXOg051Y_X7HmXrJ7vc_wMimQbA1sfE4rHtbRd45aZdAeViKWNMrvgq-14-5LSBiZqIF_jDT1Hq5acpNiF9bS57vv4xtD2AZ-IZ3j7rgAcmQLcnGCI7gCr"
        const val NOTIFICATION_ID = 1114
    }
}
/*
val alarmManager = mainActivity.getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(mainActivity, AlarmReceiver::class.java)
        intent.let{
            it.putExtra("todo",todo.toDo)
            it.putExtra("deadline",todo.deadline)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            mainActivity, NOTIFICATION_ID , intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val toastMessage = if(isChecked){

            tamagoViewModel.updateLevel()

            val time = 10000
            val triggerTime = (SystemClock.elapsedRealtime() + time) //두가지 시간중 지금은 경과시간
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, //절전모드에서도 알람 울림
                triggerTime,
                pendingIntent
            )
            "${time/1000}후 알람 울려요 "

        } else{
            alarmManager.cancel(pendingIntent)
        }
        Toast.makeText(mainActivity, "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
 */
/*
// drag and drop feature
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(todoListViewModel.todoList, fromPosition, toPosition)

            // adapter에게 item이 이동함을 알려줌
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
 */