package ru.ksart.potatohandbook.ui.potato.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.load
import coil.request.ImageRequest
import ru.ksart.potatohandbook.R
import ru.ksart.potatohandbook.databinding.FragmentPotatoDetailBinding
import ru.ksart.potatohandbook.ui.ShowMenu
import java.io.File

class PotatoDetailFragment : Fragment() {

    private var binding: FragmentPotatoDetailBinding? = null
    private val parent get() = activity?.let { it as? ShowMenu }
    private val args: PotatoDetailFragmentArgs by navArgs()
    private val item by lazy { args.item }

    private fun <T> views(block: FragmentPotatoDetailBinding.() -> T): T? = binding?.block()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // установим свою анимацию перехода
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_image)
        // отложить переход входа
        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPotatoDetailBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent?.showMenu(show = false)
        val imageShow = item.imageUri?.takeIf { it.isNotBlank() && File(it).exists() }
            ?: item.imageUrl
        views {
            // картинка
            image.apply { transitionName = item.id.toString() }
                .load(imageShow ?: "-") {
                    listener(
                        // pass two arguments
                        onSuccess = { _, _ ->
                            startPostponedEnterTransition()
                        },
                        onError = { request: ImageRequest, throwable: Throwable ->
                            startPostponedEnterTransition()
//                            request.error
                        })
                    crossfade(true)
                    placeholder(R.drawable.ic_download)
                    error(R.drawable.potato)
//                    transformations(CircleCropTransformation())
                    build()
                }
            // Название
            name.text = item.name
            description.text = item.description
            variety.text = getString(item.variety.caption)
            ripening.text = getString(item.ripening.caption)
            productivity.text = getString(item.productivity.caption)
            editPotatoButton.setOnClickListener { editItem() }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun editItem() {
        val action =
            PotatoDetailFragmentDirections.actionPotatoDetailFragmentToPotatoAddFragment(item)
        findNavController().navigate(action)
    }
}