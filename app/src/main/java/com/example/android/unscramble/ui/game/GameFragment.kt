/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragmen tempat permainan dimainkan, berisi logika permainan.
 */
class GameFragment : Fragment() {

    // Mengikat instans objek dengan akses ke tampilan dalam tata letak game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Buat ViewModel saat pertama kali fragmen dibuat.
    // Jika fragmen dibuat ulang, fragmen akan menerima instans GameViewModel yang sama yang dibuat oleh
    // fragmen pertama.
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Mengembang file XML tata letak dan mengembalikan instans objek pengikatan
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Atur viewModel untuk pengikatan data - ini memungkinkan akses tata letak terikat
        // ke semua data di VieWModel
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        // Tentukan tampilan fragmen sebagai pemilik siklus hidup pengikatan.
        // Hal ini digunakan agar pengikatan dapat mengamati pembaruan LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        // Siapkan pendengar klik untuk tombol Kirim dan Lewati.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    /*
    * Memeriksa kata-kata pengguna, dan memperbarui skor yang sesuai.
    * Menampilkan kata acak berikutnya.
    * Setelah kata terakhir, pengguna diperlihatkan Dialog dengan skor akhir.
    */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /*
     * Melewati kata saat ini tanpa mengubah skor.
     * Meningkatkan jumlah kata.
     * Setelah kata terakhir, pengguna diperlihatkan Dialog dengan skor akhir.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    /*
     * Membuat dan menampilkan AlertDialog dengan skor akhir.

     */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.you_scored, viewModel.score.value))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    exitGame()
                }
                .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                    restartGame()
                }
                .show()
    }

    /*
     * Menginisialisasi ulang data di ViewModel dan memperbarui tampilan dengan data baru, untuk
     * mulai ulang game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Keluar dari permainan.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Mengatur dan mengatur ulang status kesalahan bidang teks.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
