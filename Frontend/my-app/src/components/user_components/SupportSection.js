import ConfirmModal from '../common_components/ConfirmModal';

const [showConfirmModal, setShowConfirmModal] = useState(false);

const handleSubmit = () => {
  setShowConfirmModal(true);
};

const handleConfirmSubmit = () => {
  // Yanıt gönderme işlemi burada yapılacak
  setShowConfirmModal(false);
};

<ConfirmModal
  isOpen={showConfirmModal}
  onClose={() => setShowConfirmModal(false)}
  onConfirm={handleConfirmSubmit}
  title="Yanıt Gönder"
  message="Yanıtı göndermek istediğinizden emin misiniz?"
/> 