package com.shishir.shree.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object Supabase {
    val client = createSupabaseClient(
        supabaseUrl = "https://chbrjktgdjtstdhlwehy.supabase.co",
        supabaseKey = "sb_publishable_8nmP3F3MlifKL1xyqsCwXA_uJBZg4Mp",
    ) {
        install(Postgrest)
    }
}
