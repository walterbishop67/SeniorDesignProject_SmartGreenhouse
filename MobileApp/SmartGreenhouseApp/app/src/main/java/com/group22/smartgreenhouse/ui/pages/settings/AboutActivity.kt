package com.group22.smartgreenhouse.ui.pages.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.ui.theme.BarGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutActivity(navController: NavHostController) {

    /* ---------- Colors / style helpers ---------- */
    val tickGreen = Color(0xFF4CAF50)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.green),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            /* --- headline --------------------------------------------------- */
            Text(
                text = "Akıllı Sera Sistemi",
                fontSize = 22.sp,
                color   = BarGreen,
                fontWeight = FontWeight.Bold
            )

            /* --- intro paragraph ------------------------------------------- */
            Text(
                "Modern tarımı desteklemek ve sera yönetimini dijitalleştirerek çiftçilere zaman kazandırmak amacıyla geliştirilmiş bütünleşik bir platformdur. " +
                        "Projemiz, geleneksel sera yönetiminin zorluklarını azaltmak ve verimi arttırmak için yapay zeka, IoT ve mobil teknolojileri bir araya getirerek yenilikçi bir çözüm sunar."
            )

            /* --- feature list ---------------------------------------------- */
            Text("Sistem Özellikleri", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            val features = listOf(
                "Sıcaklık ve nem değerleri anlık olarak izlenebilir",
                "Yapay zeka destekli bitki hastalık tespiti yapılabilir",
                "Otomatik bildirim sistemi sayesinde olumsuz durumlar anında fark edilir",
                "Pazar fiyat listeleri günlük olarak görüntülenebilir",
                "Takvim ve görev planlayıcı ile seradaki işlemler kolayca takip edilebilir",
                "İsteğe bağlı olarak kamera ile görsel takip yapılabilir",
                "Uygulama içi çiftçi forumu ile bilgi alışverişi sağlanabilir"
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.forEach { line ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = tickGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(line)
                    }
                }
            }

            /* --- closing paragraph ----------------------------------------- */
            Text(
                "Mobil ve web platformları üzerinden erişilebilen bu sistem, yalnızca ölçüm yapan bir uygulama olmanın ötesine geçerek, çiftçilere karar destek mekanizması sunar. " +
                        "Aynı zamanda kullanıcı dostu arayüzü ile hem küçük ölçekli üreticilere hem de kurumsal çiftliklere hitap eder."
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutPreview() {
    //AboutActivity()
}
