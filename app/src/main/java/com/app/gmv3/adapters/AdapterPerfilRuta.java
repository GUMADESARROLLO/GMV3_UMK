package com.app.gmv3.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityEstadisticas;
import com.app.gmv3.models.FacturasMoras;
import com.app.gmv3.models.Product;
import com.app.gmv3.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterPerfilRuta extends RecyclerView.Adapter<AdapterPerfilRuta.ViewHolder>  implements Filterable {

    private Context context;
    private List<FacturasMoras> Lista_Factura_mora;
    private List<FacturasMoras> FacturaListFiltered;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_factura_id;
        TextView txt_factura_date;
        TextView txt_factura_cantidad;
        TextView txt_factura_monto;
        TextView txt_dias_vencidos;

        public ViewHolder(View view) {
            super(view);
            txt_factura_id = view.findViewById(R.id.factura_id);
            txt_factura_date = view.findViewById(R.id.factura_date);
            txt_factura_cantidad = view.findViewById(R.id.factura_cantidad);
            txt_factura_monto = view.findViewById(R.id.factura_monto);
            txt_dias_vencidos = view.findViewById(R.id.id_dias_vencidos);

        }

    }

    public AdapterPerfilRuta(Context context, List<FacturasMoras> arrayItemCart) {
        this.context = context;
        this.Lista_Factura_mora = arrayItemCart;
        this.FacturaListFiltered = arrayItemCart;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_factura_mora, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final FacturasMoras Factura = Lista_Factura_mora.get(position);

        String price = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Factura.getFactura_cant()));
        String Monto = String.format(Locale.ENGLISH, "%1$,.2f", Double.parseDouble(Factura.getFactura_monto()));

        String Nombre = Factura.getFactura_id().concat(" - ").concat(Factura.getFactura_nombre());

        //final String Fecha = (String) DateFormat.format("EEE dd MMM yyyy'", Utils.timeStringtoMilis_Factura(Factura.getFactura_date()));
        final String Fecha =  Factura.getFactura_date();

        holder.txt_factura_id.setText(("Nº ").concat(Nombre));
        holder.txt_factura_date.setText(Fecha);
        holder.txt_factura_cantidad.setText(("C$ ").concat(price));
        holder.txt_factura_monto.setText(("C$ ").concat(Monto));

        double Meta = Double.parseDouble(Factura.getFactura_devencidos());

        String lbl_dias = String.format(Locale.ENGLISH, "%1$,.0f",  Math.abs(Meta));
        String lbl_mora = ( Meta < 0 ? ("En  ").concat(lbl_dias).concat(" dias se vence.") : ("Tiene  ").concat(lbl_dias).concat(" dias de vencida.") );

        holder.txt_dias_vencidos.setText(lbl_mora);
        holder.txt_dias_vencidos.setTextColor(Meta < 0 ? ContextCompat.getColor(context,R.color.grey_60): ContextCompat.getColor(context,R.color.red));

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    FacturaListFiltered = Lista_Factura_mora;
                } else {
                    List<FacturasMoras> filteredList = new ArrayList<>();
                    for (FacturasMoras row : Lista_Factura_mora) {
                        if (row.getFactura_nombre().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    FacturaListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FacturaListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                FacturaListFiltered = (ArrayList<FacturasMoras>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Product product);
    }
    @Override
    public int getItemCount() {
        return Lista_Factura_mora.size();
    }



}
